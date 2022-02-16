package io.github.woodenlock.gaia;

import io.github.woodenlock.gaia.annotation.DynamicModule;
import io.github.woodenlock.gaia.annotation.EnableDynamicModules;
import io.github.woodenlock.gaia.annotation.GenerateComponent;
import io.github.woodenlock.gaia.common.GenerateConst;
import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import io.github.woodenlock.gaia.util.NamingUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * 动态模块注册器
 *
 * @author woodenlock
 * @date 2021/12/2 10:30
 */
public class DynamicModuleRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, ResourceLoaderAware {
    private static final Logger log = LoggerFactory.getLogger(DynamicModuleRegistrar.class);

    /**
     * 配置属性
     **/
    private DynamicModuleProperties properties;

    /**
     * spring提供的资源表达式匹配处理器
     **/
    private ResourcePatternResolver resolver;

    /**
     * spring提供的元数据读取器
     **/
    private MetadataReaderFactory metaReader;

    @Override
    public void registerBeanDefinitions(@NonNull AnnotationMetadata metadata,
        @NonNull BeanDefinitionRegistry registry) {
        AnnotationAttributes attributes =
            AnnotationAttributes.fromMap(metadata.getAnnotationAttributes(EnableDynamicModules.class.getName()));
        Assert.notNull(attributes, "Failed to load config from enable annotation due to fail to read attributes.");
        boolean enable = (boolean)attributes.get(NamingUtils.getRawProperty(EnableDynamicModules::enable));
        if(null != properties) {
            resetConfig(attributes, ClassUtils.getPackageName(metadata.getClassName()));
        }
        log.debug("Find enable:{} option with configs:{} of dynamic modules.", enable, properties);
        if (!enable || null == properties) {
            return;
        }

        //扫描所有持久化映射类
        Set<String> includes = new HashSet<>();
        Optional.ofNullable(properties.getIncludes()).ifPresent(
            arr -> Arrays.stream(arr).forEach(in -> includes.addAll(scanEntityClasses(in, resolver, metaReader))));
        Optional.ofNullable(properties.getExcludes()).ifPresent(arr -> Arrays.stream(arr)
            .forEach(in -> scanEntityClasses(in, resolver, metaReader).forEach(includes::remove)));

        //解析所有持久化映射类
        Set<Class<?>> entities = new HashSet<>();
        includes.stream().distinct().forEach(name -> {
            //获取类实例
            Class<?> clazz;
            try {
                clazz = Class.forName(name);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(
                    "Failed to find class for dynamic module class[" + name + "] due to exception:" + e.getMessage());
            }

            //忽略无注解或注解中指定不启用的类
            DynamicModule module =
                clazz.isAnnotationPresent(DynamicModule.class) ? clazz.getAnnotation(DynamicModule.class) : null;
            if (null != module && module.enable()) {
                entities.add(clazz);
                analyzeEntity(clazz, module);
            }
        });
        log.info("Finished to analyze all dynamic modules:{}.", entities);

        //执行具体生成、注册操作
        ModuleTypeCache.action(registry);
    }

    /**
     * 分析单个持久化映射类的所有目标组件
     *
     * @param clazz       持久化映射类
     * @param module      持久化映射类使用的注解
     * @see ModuleTypeCache
     */
    private void analyzeEntity(Class<?> clazz, DynamicModule module) {
        //持久化方案优先注解中的其次默认配置
        String persistence =
            DynamicModuleUtils.isNotBlank(module.value()) ? module.value() : properties.getDefaultPersistence();
        if (DynamicModuleUtils.isBlank(persistence)) {
            throw new RuntimeException(
                String.format("Failed to analyze dynamic module of the class:%s due to lack of persistence.", clazz));
        }

        //默认偏好可用的组件信息
        Map<String, GenerateComponent> components = new HashMap<>(16);
        Optional.ofNullable(properties.getDefaultComponents())
            .ifPresent(list -> list.forEach(co -> components.put(co, null)));
        //解析该类所有指定的组件注解
        for (GenerateComponent com : clazz.getAnnotationsByType(GenerateComponent.class)) {
            if (com.generated()) {
                components.put(com.type(), com);
            } else {
                components.remove(com.type());
            }
        }

        //遍历所有目标组件
        components.forEach((k, v) -> {
            Part part = new Part();
            part.setEntity(clazz);
            //优先指定的注解
            if(null != v){
                part.setSuffix(v.suffix());
                part.setPath(v.path());
                part.setGenerate(BooleanEnum.of(v.generated()));
                part.setGeneratorPath(v.generatorPath());
                part.setRegister(BooleanEnum.of(v.registered()));
                part.setRegistrarPath(v.registrarPath());
            }
            //其次为配置中的偏好
            Preference[] matched = properties.getPreferences().stream()
                .filter(p -> (DynamicModuleUtils.isBlank(p.getPersistence()) || p.getPersistence().equals(persistence))
                    && (DynamicModuleUtils.isBlank(p.getComponent()) || p.getComponent().equals(k)))
                .sorted(Comparator.comparingInt(Preference::getOrder).reversed())
                .toArray(Preference[]::new);
            mapNodeValue(part, matched);

            //重写包路径，加上默认配置的包前缀路径
            String split = "/";
            String fullPackage = properties.getDefaultPrefixPath();
            fullPackage = null == fullPackage ? "" : fullPackage.trim();
            if(!fullPackage.startsWith(split)){
                fullPackage = split + fullPackage;
            }
            if(!fullPackage.endsWith(split)){
                fullPackage += split;
            }
            if(DynamicModuleUtils.isNotBlank(part.getPath())){
                fullPackage += part.getPath();
            }
            if(!fullPackage.endsWith(split)){
                fullPackage += split;
            }
            part.setPath(fullPackage.replaceAll("\\.", "/").replaceAll("//", "/"));
            part.setSuffix(null == part.getSuffix() ? "" : part.getSuffix().trim());
            String gen = part.getGeneratorPath();
            gen = null == gen ? null : gen.trim().replaceAll("/", "\\.").replaceAll("\\.\\.", "\\.");
            part.setGeneratorPath(gen);
            String reg = part.getRegistrarPath();
            reg = null == reg ? null : reg.trim().replaceAll("/", "\\.").replaceAll("\\.\\.", "\\.");
            part.setRegistrarPath(reg);

            //待处理的组件封装对象存入缓存
            ModuleTypeCache.putComponentCache(clazz, k, part);
        });
    }

    /**
     * 扫描动态加载类的持久化映射对象类全路径集合
     *
     * @param path       单词扫描路径
     * @param resolver   扫描器
     * @param metaReader 元数据工厂
     * @return java.util.List<java.lang.String>
     */
    private List<String> scanEntityClasses(String path, ResourcePatternResolver resolver,
        MetadataReaderFactory metaReader) {
        List<String> result = new ArrayList<>();
        if (DynamicModuleUtils.isNotBlank(path)) {
            ClassMetadata cm;
            String fileName;
            path = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + path;
            try {
                Resource[] resources = resolver.getResources(path);
                List<String>ignores = new ArrayList<>();
                for (Resource re : resources) {
                    if (null == (fileName = re.getFilename()) || !fileName.endsWith(".class")) {
                        ignores.add(fileName);
                    } else if ((cm = metaReader.getMetadataReader(re).getClassMetadata()).isConcrete()) {
                        result.add(cm.getClassName());
                    }
                }
                if(DynamicModuleUtils.isNotEmpty(ignores)){
                    log.debug("Ignore useless files:{} when scan dynamic module entity class on:{}.", ignores, path);
                }
            } catch (IOException e) {
                throw new RuntimeException(
                    "Failed to scan path:'" + path + "' of dynamic module due to exception:" + e.getMessage());
            }
        }

        return result;
    }

    /**
     * 重置配置信息，用启动注解指定的属性来覆盖配置文件中的属性
     * @param attributes 入口注解属性集
     * @param entrance 入口注解体现的包路径
     */
    private void resetConfig(@NonNull AnnotationAttributes attributes, @NonNull String entrance) {
        String[] annIncludes = (String[])attributes.get(NamingUtils.getRawProperty(EnableDynamicModules::includes));
        if (annIncludes.length != 0) {
            properties.setIncludes(annIncludes);
        }
        if (null == properties.getIncludes() || properties.getIncludes().length == 0) {
            properties.setIncludes(new String[] {entrance.replaceAll("\\.", "/") + "/**"});
        }
        if(DynamicModuleUtils.isBlank(properties.getDefaultPrefixPath())){
            properties.setDefaultPrefixPath(entrance);
        }
        Optional.ofNullable(attributes.get(NamingUtils.getRawProperty(EnableDynamicModules::excludes)))
            .ifPresent(excludes -> {
                if (((String[])excludes).length != 0) {
                    properties.setExcludes((String[])excludes);
                }
            });
        Preference[] array = properties.getDefaultPreferences();
        if(null != array && array.length != 0){
            if(null == properties.getPreferences()){
                properties.setPreferences(new ArrayList<>());
            }
            List<Preference> preferences = properties.getPreferences();
            preferences.addAll(Arrays.asList(array));
            properties.setDefaultPreferences(null);
        }
    }

    /**
     * 如果目标节点的属性值缺失则使用来源对象来覆盖
     *
     * @param target 目标节点
     * @param sources 来源对象数组，排序靠前的优先覆盖
     */
    private void mapNodeValue(Node target, Node... sources) {
        for (Node source : sources) {
            if (DynamicModuleUtils.isBlank(target.getSuffix())) {
                target.setSuffix(source.getSuffix());
            }
            if (DynamicModuleUtils.isBlank(target.getPath())) {
                target.setPath(source.getPath());
            }
            if (BooleanEnum.IGNORE.equals(target.getGenerate())) {
                target.setGenerate(source.getGenerate());
            }
            if (DynamicModuleUtils.isBlank(target.getGeneratorPath())) {
                target.setGeneratorPath(source.getGeneratorPath());
            }
            if (BooleanEnum.IGNORE.equals(target.getRegister())) {
                target.setRegister(source.getRegister());
            }
            if (DynamicModuleUtils.isBlank(target.getRegistrarPath())) {
                target.setRegistrarPath(source.getRegistrarPath());
            }
            if(null == target.getCustomizes() || target.getCustomizes().isEmpty()) {
                target.setCustomizes(source.getCustomizes());
            }
        }
    }

    @Override
    public void setEnvironment(@NonNull Environment environment) {
        BindResult<DynamicModuleProperties> result =
            Binder.get(environment).bind(GenerateConst.Config.PREFIX, Bindable.of(DynamicModuleProperties.class));
        if (result.isBound()) {
            properties = result.get();
        }
    }

    @Override
    public void setResourceLoader(@NonNull ResourceLoader resourceLoader) {
        resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
        metaReader = new CachingMetadataReaderFactory(resourceLoader);
    }
}