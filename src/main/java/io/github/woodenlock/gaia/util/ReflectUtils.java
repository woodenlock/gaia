package io.github.woodenlock.gaia.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

/**
 * 反射相关的工具类
 *
 * @author woodenlock
 * @date 2021/5/5 19:11
 */
@SuppressWarnings("unused")
public enum ReflectUtils {
    ;

    private static final Logger log = LoggerFactory.getLogger(ReflectUtils.class);

    /**
     * 类的类信息属性名{@link Object#getClass()}
     */
    public static final String CLASS_NAME = Class.class.getSimpleName().toLowerCase();

    /**
     * 判断一个类是否是具体的
     *
     * @param clazz 目标类
     * @return boolean
     */
    public static boolean isConcrete(Class<?> clazz) {
        return null != clazz && !Modifier.isAbstract(clazz.getModifiers()) && !ClassUtils.isPrimitiveWrapper(clazz)
            && !ClassUtils.isPrimitiveWrapper(clazz.getSuperclass());
    }

    /**
     * 获取泛型类的对象的泛型数组
     *
     * @param source 运行时的泛型类的对象
     * @param supper 所属的带泛型类型的类
     * @return java.lang.Class<?>[]
     */
    public static <T> Class<?>[] getRawTypes(T source, Class<T> supper) {
        Class<?>[] result = null;
        if (null != source && null != supper) {
            ResolvableType[] generics = ResolvableType.forClass(source.getClass()).as(supper).getGenerics();
            result = new Class<?>[generics.length];
            for (int i = 0; i < generics.length; i++) {
                result[i] = generics[i].resolve();
            }
        }

        return result;
    }

    /**
     * 类属性缓存
     **/
    private static final Map<Class<?>, Map<String, PropertyDescriptor>> PROPERTY_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取类的所有属性（带缓存，暂不考虑属性修剪）
     *
     * @param clazz 目标类
     * @return java.util.Map<java.lang.String, java.beans.PropertyDescriptor>
     */
    public static Map<String, PropertyDescriptor> getProperties(Class<?> clazz) {
        Map<String, PropertyDescriptor> result = new HashMap<>(16);
        if (isConcrete(clazz)) {
            result = PROPERTY_CACHE.computeIfAbsent(clazz, key -> Arrays.stream(BeanUtils.getPropertyDescriptors(clazz))
                .filter(p -> !CLASS_NAME.equals(p.getName()))
                .collect(Collectors.toMap(PropertyDescriptor::getName, pro -> pro)));
            result = new HashMap<>(result);
        }

        return result;
    }

    /**
     * 类字段缓存
     **/
    private static final Map<Class<?>, List<Field>> FIELD_CACHE = new ConcurrentHashMap<>();

    /**
     * 获取目标类的所有成员属性（带缓存，暂不考虑属性修剪）
     *
     * @param clazz 目标类
     * @return java.util.List<java.lang.reflect.Field>
     */
    public static List<Field> getAllDeclaredFields(Class<?> clazz) {
        List<Field> result = new ArrayList<>();
        if (isConcrete(clazz)) {
            List<Field> cache = FIELD_CACHE.get(clazz);
            if(null == cache){
                cache = new ArrayList<>();
                do {
                    for (Field f : clazz.getDeclaredFields()) {
                        if(!CLASS_NAME.equals(f.getName())) {
                            f.setAccessible(true);
                            cache.add(f);
                        }
                    }
                    clazz = clazz.getSuperclass();
                }
                while(clazz != Object.class);
                FIELD_CACHE.put(clazz, cache);
            }
            result = new ArrayList<>(cache);
        }

        return result;
    }

    /**
     * 对象类型强转
     *
     * @param source           原始对象
     * @param targetType       目标类型
     * @param ignoreProperties 原始对象需要忽略的属性
     * @return R
     */
    public static <R> R conversion(Object source, Class<R> targetType, String... ignoreProperties) {
        R result = null;
        if (null != source && isConcrete(targetType)) {
            try {
                R target = targetType.newInstance();
                mapping(source, target, (s, t) -> {
                    try {
                        Object value = s.getReadMethod().invoke(source);
                        if (null != t) {
                            t.getWriteMethod().invoke(target, value);
                        }
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        String temp = "Could not copy property from source[%s] to target[%s] when conversion.";
                        throw new FatalBeanException(String.format(temp, source, target), e);
                    }
                }, null, ignoreProperties);
                result = target;
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Failed to convert:{} to target type:{} due to exception:{}.", source, targetType,
                    e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    /**
     * 集合类型强转成特定类型的ArrayList
     *
     * @param collection       原始对象集合
     * @param targetType       目标类型
     * @param ignoreProperties 原始对象需要忽略的属性
     * @return ArrayList<R>
     */
    public static <R, A> List<R> conversionList(Collection<A> collection, Class<R> targetType,
        String... ignoreProperties) {
        List<R> result = null;
        if (DynamicModuleUtils.isNotEmpty(collection) && isConcrete(targetType)) {
            result = new ArrayList<>(collection.size());
            try {
                for (A source : collection) {
                    R single = targetType.newInstance();
                    mapping(source, single, (s, t) -> {
                        try {
                            Object value = s.getReadMethod().invoke(source);
                            if (null != t) {
                                t.getWriteMethod().invoke(single, value);
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            String temp = "Could not copy property from source[%s] to target[%s] when conversion list.";
                            throw new FatalBeanException(String.format(temp, source, single), e);
                        }
                    }, null, ignoreProperties);
                    result.add(single);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                log.error("Failed to convert collection:{} to target type:{} due to exception:{}.", collection,
                    targetType, e.getMessage());
                throw new RuntimeException(e);
            }
        }

        return result;
    }

    /**
     * bean mapper属性映射通用处理
     *
     * @param source           原始参照对象
     * @param target           目标写入对象
     * @param action           具体的业务操作
     * @param consumer         不可写入自定义处理
     * @param ignoreProperties 忽略的属性名
     */
    public static <T, R> void mapping(T source, R target, BiConsumer<PropertyDescriptor, PropertyDescriptor> action,
        BiConsumer<PropertyDescriptor, PropertyDescriptor> consumer, @Nullable String... ignoreProperties) {
        if (null != source && isConcrete(source.getClass()) && null != target && isConcrete(target.getClass())
            && null != action) {
            Class<?> sourceClazz = source.getClass();
            Class<?> targetClazz = target.getClass();
            Map<String, PropertyDescriptor> sourcePds = getProperties(sourceClazz);
            Map<String, PropertyDescriptor> targetPds = getProperties(targetClazz);

            Set<String> writeNames = targetPds.keySet();
            List<String> ignores = ignoreProperties != null ? Arrays.asList(ignoreProperties) : new ArrayList<>();
            ignores.forEach(writeNames::remove);
            sourcePds.entrySet().stream().filter(en -> writeNames.contains(en.getKey())).forEach(en -> {
                PropertyDescriptor prop = en.getValue();
                Method readMethod = prop.getReadMethod();
                PropertyDescriptor targetProp = targetPds.get(en.getKey());
                Method writeMethod = targetProp.getWriteMethod();
                boolean access = null != readMethod && null != writeMethod && ClassUtils
                    .isAssignable(writeMethod.getParameterTypes()[0], readMethod.getReturnType());
                if (access && prop.getPropertyType().getTypeParameters().length != 0) {
                    //泛型类型则需要额外比较signature中的泛型类名称数组中每一个类都相同或者具有继承关系
                    String ss = readMethod.getGenericReturnType().getTypeName();
                    String[] sa = ss.substring(ss.indexOf("<") + 1, ss.indexOf(">")).split(",");
                    String ts = writeMethod.getGenericParameterTypes()[0].getTypeName();
                    String[] ta = ts.substring(ts.indexOf("<") + 1, ts.indexOf(">")).split(",");
                    if (access = sa.length == ta.length) {
                        try {
                            for (int i = 0; i < sa.length; i++) {
                                access &= sa[i].equals(ta[i]) || ClassUtils
                                    .isAssignable(Class.forName(sa[i]), (Class.forName(ta[i])));
                            }
                        } catch (ClassNotFoundException e) {
                            log.error("Exception:{} occurred when mapping prop:{} of class:{}", e.getMessage(),
                                prop.getDisplayName(), sourceClazz);
                            access = false;
                        }
                    }
                }
                if (access) {
                    if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                        readMethod.setAccessible(true);
                    }
                    if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                        writeMethod.setAccessible(true);
                    }
                    action.accept(prop, targetProp);
                } else if (null != consumer) {
                    consumer.accept(prop, targetProp);
                }
            });
        }
    }

    /**
     * 加载类
     *
     * @param name   类的全路径
     * @param bytes  类字节
     * @param loader 使用的加载器，为空的时候使用默认的系统加载器
     * @return java.lang.Class<?>
     */
    public static Class<?> load(String name, byte[] bytes, ClassLoader loader) {
        return ClassLoaderHolder.load(name, bytes, loader);
    }

    /**
     * 查找已加载的类
     *
     * @param name   类的全限定名
     * @param loader 使用的加载器，为空的时候使用默认的系统加载器
     * @return 类信息
     */
    public static Class<?> find(String name, ClassLoader loader) {
        return ClassLoaderHolder.find(name, loader);
    }

    /**
     * 手动初始化类的包
     *
     * @param clazz 类信息
     * @return 目标类所属的包命名空间
     */
    @SuppressWarnings("UnusedReturnValue")
    public static Package forPackage(Class<?> clazz) {
        return ClassLoaderHolder.forPackage(clazz);
    }

    /**
     * 持有加载器加载方法的静态内部类
     **/
    private static class ClassLoaderHolder {

        static final Method LOAD_METHOD;

        static final Method FIND_METHOD;

        static final Method DEFINE_METHOD;

        static final Method GET_METHOD;

        static {
            try {
                LOAD_METHOD = ClassLoader.class
                    .getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
                LOAD_METHOD.setAccessible(true);
                FIND_METHOD = ClassLoader.class.getDeclaredMethod("findLoadedClass", String.class);
                FIND_METHOD.setAccessible(true);
                DEFINE_METHOD = ClassLoader.class
                    .getDeclaredMethod("definePackage", String.class, String.class, String.class, String.class,
                        String.class, String.class, String.class, URL.class);
                DEFINE_METHOD.setAccessible(true);
                GET_METHOD = ClassLoader.class.getDeclaredMethod("getPackage", String.class);
                GET_METHOD.setAccessible(true);
            } catch (NoSuchMethodException | SecurityException e) {
                throw new RuntimeException(
                    "Failed to init ClassLoaderHolder due to fail to resolve inner method.Error:" + e.getMessage());
            }
        }

        /**
         * 加载类
         *
         * @param name   类的全路径
         * @param bytes  类字节
         * @param loader 使用的加载器，为空的时候使用默认的系统加载器
         * @return java.lang.Class<?>
         */
        static Class<?> load(String name, byte[] bytes, ClassLoader loader) {
            Class<?> result = null;
            if (DynamicModuleUtils.isNotBlank(name) && null != bytes && bytes.length != 0) {
                loader = null == loader ? ClassLoader.getSystemClassLoader() : loader;
                String finalName = name.replaceAll("/", "\\.");
                //先查找后加载，防止被加载器重复加载导致报错
                result = find(finalName, loader);
                if (null == result) {
                    try {
                        result = (Class<?>)LOAD_METHOD.invoke(loader, finalName, bytes, 0, bytes.length);
                    } catch (IllegalAccessException | InvocationTargetException ex) {
                        String message =
                            String.format("Failed to load class:%s due to exception:%s", name, ex.getMessage());
                        throw new RuntimeException(message);
                    }
                }
            }

            return result;
        }

        /**
         * 查找已加载的类
         *
         * @param name   类的全限定名
         * @param loader 使用的加载器，为空的时候使用默认的系统加载器
         * @return 类信息
         */
        static Class<?> find(String name, ClassLoader loader) {
            Class<?> result = null;
            if (DynamicModuleUtils.isNotBlank(name)) {
                loader = null == loader ? ClassLoader.getSystemClassLoader() : loader;
                try {
                    result = (Class<?>)FIND_METHOD.invoke(loader, name);
                } catch (IllegalAccessException | InvocationTargetException ex) {
                    log.error("Failed to get loaded class:{} due to exception:{}.", name, ex.getMessage(), ex);
                }
            }

            return result;
        }

        /**
         * 寻找并链接包信息
         *
         * @param clazz 目标类
         * @return 目标类所属的包命名空间
         */
        static Package forPackage(Class<?> clazz) {
            Package result = null;
            if (null != clazz) {
                ClassLoader loader = clazz.getClassLoader();
                String name = clazz.getCanonicalName().substring(0, clazz.getCanonicalName().lastIndexOf("."));
                try {
                    result = (Package)GET_METHOD.invoke(loader, name);
                    if (null == result) {
                        DEFINE_METHOD.invoke(loader, name, null, null, null, null, null, null, null);
                        result = (Package)GET_METHOD.invoke(loader, name);
                    }
                } catch (IllegalAccessException | InvocationTargetException e) {
                    log.error("Failed to look up package for class:{} due to exception:{}.", clazz.getCanonicalName(),
                        e.getMessage());
                }
            }

            return result;
        }
    }
}