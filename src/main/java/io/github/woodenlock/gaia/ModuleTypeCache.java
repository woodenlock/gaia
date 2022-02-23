package io.github.woodenlock.gaia;

import io.github.woodenlock.gaia.registrar.BeanRegistrar;
import io.github.woodenlock.gaia.generation.ClassGenerator;
import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import io.github.woodenlock.gaia.util.ReflectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.NonNull;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 动态模块类型缓存
 *
 * @author woodenlock
 * @date 2021/12/11 13:57
 */
public enum ModuleTypeCache {
    ;
    private static final Logger log = LoggerFactory.getLogger(ModuleTypeCache.class);

    /**
     * 根据动态模块类信息与组件类型名称获取对应的组件注册到spring容器中的id
     *
     * @param moduleClass 对应的持久化实体映射类
     * @param component   组件类型名称
     * @return spring容器中的bean id
     */
    @SuppressWarnings("unused")
    public static String getComponentId(Class<?> moduleClass, String component) {
        String result = null;
        if (null != moduleClass && DynamicModuleUtils.isNotBlank(component)) {
            Map<String, Part> map = Holder.STORE.get(moduleClass);
            Part part = null == map ? null : map.get(component);
            result = null == part ? null : part.getBeanId();
        }

        return result;
    }

    /**
     * 根据动态模块类信息与组件类型名称获取对应的组件实际类信息
     *
     * @param moduleClass 对应的持久化实体映射类
     * @param component   组件类型名称
     * @return 组件实际类信息
     */
    @SuppressWarnings("unused")
    public static Class<?> getComponentType(Class<?> moduleClass, String component) {
        Class<?> result = null;
        if (null != moduleClass && DynamicModuleUtils.isNotBlank(component)) {
            Map<String, Part> map = Holder.STORE.get(moduleClass);
            Part part = null == map ? null : map.get(component);
            result = null == part ? null : part.getClazz();
        }

        return result;
    }

    /**
     * 根据动态模块类信息与组件类型名称尝试获取类全路径
     * 如果已经存在类则直接返回类信息的全路径，如果不存在则通过配置的构造信息拼接出预期的全路径
     * 仅用于动态生成模块时候类信息发生循环依赖时对类信息依赖的延迟解耦，不建议业务代码中调用
     *
     * @param moduleClass 对应的持久化实体映射类
     * @param component   组件类型名称
     * @return java.lang.String
     */
    public static String acquireCanonicalName(Class<?> moduleClass, String component) {
        String result = null;
        if (null != moduleClass && DynamicModuleUtils.isNotBlank(component)) {
            Map<String, Part> map = Holder.STORE.get(moduleClass);
            Part part = null == map ? null : map.get(component);
            if (null != part) {
                if (null == part.getClazz()) {
                    String baseName = part.getEntity().getSimpleName().trim();
                    String suffix = part.getSuffix();
                    String path = "";
                    if(null != part.getPath()){
                        path = part.getPath().trim();
                        path = path.substring(1).replaceAll("/", ".");
                    }
                    result = path + baseName + suffix;
                } else {
                    result = part.getClazz().getCanonicalName();
                }
            }
        }

        return result;
    }

    /**
     * 放置一个解析后的动态模块目标组件封装对象
     *
     * @param moduleClass 对应的持久化实体映射类
     * @param component   组件类型名称
     * @param part        组件封装对象
     */
    static void putComponentCache(Class<?> moduleClass, String component, Part part) {
        if (null != moduleClass && DynamicModuleUtils.isNotBlank(component) && null != part) {
            Holder.STORE.putIfAbsent(moduleClass, new HashMap<>(16));
            Map<String, Part> map = Holder.STORE.get(moduleClass);
            map.put(component, part);
        }
    }

    /**
     * 执行所有类的类生成、bean注册操作
     *
     * @param registry spring注册中心
     */
    static void action(@NonNull BeanDefinitionRegistry registry) {
        AtomicInteger geCounter = new AtomicInteger(0);
        AtomicInteger reCounter = new AtomicInteger(0);
        String template = "Component:%s.Generated:%s.Registered:%s";
        log.debug("Ready to operate dynamic module cache:{}.", Holder.STORE);
        Holder.STORE.forEach((type, map) -> {
            StringJoiner sj = new StringJoiner("\n");
            map.forEach((com, part) -> {
                if(!BooleanEnum.TRUE.equals(part.getGenerate())) {
                    return;
                }
                Class<?> entity = part.getEntity();
                ClassGenerator generator = loadInstance(part.getGeneratorPath(), ClassGenerator.class, Holder.GENERATOR);
                if (null == generator) {
                    throw new RuntimeException(String
                        .format("Failed to load the dynamic module:%s due to fail to load suitable generator:%s of:%s.",
                            entity, part.getGeneratorPath(), com));
                }
                Class<?> target = generator.apply(part);
                if(null == target){
                    throw new RuntimeException(String
                        .format("Failed to load the dynamic module:%s due to fail to generate class with config:%s of:%s.",
                            entity, part, com));
                }
                try {
                    Class.forName(target.getCanonicalName());
                    ReflectUtils.forPackage(target);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(String
                        .format("Failed to load the dynamic module:%s due to fail to reset class with config:%s of:%s by:%s.",
                            entity, part, com, e.getMessage()));
                }
                part.setClazz(target);
                boolean reg = BooleanEnum.TRUE.equals(part.getRegister());
                geCounter.incrementAndGet();
                if (reg) {
                    BeanRegistrar registrar = loadInstance(part.getRegistrarPath(), BeanRegistrar.class, Holder.REGISTRAR);
                    if (null == registrar) {
                        throw new RuntimeException(String
                            .format("Failed to load the dynamic module:%s " + "due to fail to load suitable registrar:%s of:%s.",
                                entity, part.getRegistrarPath(), com));
                    }
                    part.setBeanId(registrar.apply(registry, target));
                    reCounter.incrementAndGet();
                }
                sj.add(String.format(template, com, target.getCanonicalName(), reg));
            });
            log.debug("Success in operating on the dynamic module of class:{} with:\n{}.", type.getCanonicalName(), sj);
        });
        log.info("Finish to act generating class:{} and register class:{}.", geCounter.get(), reCounter.get());
    }

    @SuppressWarnings("unchecked")
    private static <T> T loadInstance(String classPath, Class<T> type, Map<String, T> cache) {
        T result = null;
        if (DynamicModuleUtils.isNotBlank(classPath)) {
            if (null == (result = cache.get(classPath))) {
                try {
                    Class<?> clazz = Class.forName(classPath);
                    Object instance = clazz.newInstance();
                    if (type.isAssignableFrom(clazz)) {
                        cache.put(classPath, result = (T)instance);
                    }
                } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException(
                        "Failed to load instance for class[" + classPath + "] by dynamic module due to exception:" + e
                            .getMessage());
                }
            }
        }

        return result;
    }

    /**
     * 实体类缓存实例的包装类
     *
     * @author woodenlock
     * @date 2021/12/11 14:00
     */
    private static class Holder {

        /**
         * 动态模块目标类缓存实例
         */
        private static final Map<Class<?>, Map<String, Part>> STORE = new HashMap<>();

        /**
         * 生成器类缓存实例
         */
        private static final Map<String, ClassGenerator> GENERATOR = new HashMap<>();

        /**
         * spring bean注册器类缓存实例
         */
        private static final Map<String, BeanRegistrar> REGISTRAR = new HashMap<>();
    }
}