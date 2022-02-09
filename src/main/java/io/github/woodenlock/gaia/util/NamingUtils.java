package io.github.woodenlock.gaia.util;

import io.github.woodenlock.gaia.function.SerializableFunction;
import io.github.woodenlock.gaia.function.SerializableBiConsumer;

import java.beans.Introspector;
import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 命名转换工具类
 *
 * @author zhangpeijun[zhangpeijun1024@qq.com]
 * @version [v0.0.1, 2017年10月17日]
 * @see [相关类/方法]
 * @since [产品/模块版本]
 */
@SuppressWarnings("unused")
public enum NamingUtils {
    ;

    /**
     * 驼峰分割标识的下划线开头字符串
     **/
    private static final Pattern LINE_PATTERN = Pattern.compile("_(\\w)");

    /**
     * 大写字母匹配
     **/
    private static final Pattern HUMP_PATTERN = Pattern.compile("[A-Z]");

    /**
     * 字符串首字符变大小写
     *
     * @param origin 原始字符串
     * @param upper  true大写，false小写
     * @return java.lang.String
     */
    public static String convertFirst(String origin, boolean upper) {
        String result = origin;
        if (DynamicModuleUtils.isNotBlank(result)) {
            String first = result.substring(0, 1);
            result = (upper ? first.toUpperCase() : first.toLowerCase()) + result.substring(1);
        }

        return result;
    }

    /**
     * 下划线转驼峰
     *
     * @param origin 原始字符串
     * @return String
     */
    public static String lineToHump(String origin) {
        origin = origin.toLowerCase();
        Matcher matcher = LINE_PATTERN.matcher(origin);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param str 原始字符串
     * @return String
     */
    public static String humpToLine(String str) {
        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    /**
     * 驼峰命名转指定分隔符的命名方式
     *
     * @param str   带替换字符串
     * @param split 分隔符
     * @return String [返回类型说明]
     * @see [类、类#方法、类#成员]
     */
    public static String humpToSplit(String str, String split) {
        Matcher matcher = HUMP_PATTERN.matcher(str);
        StringBuffer sb = new StringBuffer();
        while(matcher.find()) {
            matcher.appendReplacement(sb, split + matcher.group(0).toLowerCase());
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 获取特定长度的uuid
     *
     * @param length 截取长度
     */
    public static String getUuid(int length) {
        length = Math.max(0, length);
        length = Math.min(31, length);
        return getUuid().substring(0, length);
    }

    /**
     * 获取uuid
     *
     * @return java.lang.String
     */
    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 获取类全路径
     *
     * @param className 类全限定名
     * @return java.lang.String
     */
    public static String getPathByClassName(String className) {
        return DynamicModuleUtils.isBlank(className) ? className : className.replace(".", "/");
    }

    /**
     * 获取类全限定名
     *
     * @param path 类全路径
     * @return java.lang.String
     */
    public static String getClassNameByPath(String path) {
        return DynamicModuleUtils.isBlank(path) ? path : path.replace("/", ".");
    }

    /**
     * 根据大写字母来切割字符串
     *
     * @param original 原始字符串
     * @return java.util.List<java.lang.String>
     */
    public static List<String> getSplitsByUpperCase(String original) {
        List<String> result = null;
        if (null != original) {
            result = new ArrayList<>();
            int index = -1;
            int last = 0;
            while(++index < original.length()) {
                if (original.charAt(index) >= 'A' && original.charAt(index) <= 'Z' && index != last) {
                    result.add(original.substring(last, index));
                    last = index;
                }
            }
            if (index != last) {
                result.add(original.substring(last, index));
            }
        }

        return result;
    }

    private static final Pattern GET_PATTERN = Pattern.compile("^get[A-Z].*");

    private static final Pattern SET_PATTERN = Pattern.compile("^set[A-Z].*");

    private static final Pattern IS_PATTERN = Pattern.compile("^is[A-Z].*");

    /**
     * 以序列化lambda方式获取下划线格式的属性字段名，用于兼容静态泛型调用
     *
     * @param func 属性lambda函数
     * @return java.lang.String
     */
    public static <T, R> String getRawColumn(SerializableFunction<T, R> func) {
        return getColumn(func);
    }

    /**
     * 以lambda方式获取下划线格式的属性字段名
     *
     * @param func         属性lambda函数
     * @param defaultValue 默认值
     * @return java.lang.String
     */
    public static <T, R, F extends Function<T, R>> String getColumn(F func, String defaultValue) {
        String real = getColumn(func);
        return null == real ? defaultValue : real;
    }

    /**
     * 以lambda方式获取下划线格式的属性字段名
     *
     * @param func 属性lambda函数
     * @return java.lang.String
     */
    public static <T, R, F extends Function<T, R>> String getColumn(F func) {
        String property = getProperty(func);

        return DynamicModuleUtils.isBlank(property) ? null : humpToLine(property);
    }

    /**
     * 以序列化lambda方式获取属性字段名，用于兼容静态泛型调用
     *
     * @param func 属性lambda函数
     * @return java.lang.String
     */
    public static <T, R> String getRawProperty(SerializableFunction<T, R> func) {
        return getProperty(func);
    }

    /**
     * 以lambda方式获取getter对应的属性名，要求对应的getter方法命名为getXXX或者isXXX
     * 用于消除对应的魔法字符，使用反射，暂未缓存，效率一般
     *
     * @param func         属性lambda函数
     * @param defaultValue 默认值
     * @return getter对应的属性名
     */
    public static <T, R, F extends Function<T, R>> String getProperty(F func, String defaultValue) {
        String real = getProperty(func);
        return null == real ? defaultValue : real;
    }

    /**
     * 以lambda方式获取getter对应的属性名，要求对应的getter方法命名为getXXX或者isXXX
     * 用于消除对应的魔法字符，使用反射，暂未缓存，效率一般
     *
     * @param func 属性lambda函数
     * @return getter对应的属性名
     */
    public static <T, R, F extends Function<T, R>> String getProperty(F func) {
        String result = null;
        if (func instanceof Serializable) {
            String getter = getLambdaMethodName((Serializable)func);
            if (null != getter) {
                if (GET_PATTERN.matcher(getter).matches()) {
                    getter = getter.substring(3);
                } else if (IS_PATTERN.matcher(getter).matches()) {
                    getter = getter.substring(2);
                }
                result = Introspector.decapitalize(getter);
            }
        }

        return result;
    }

    /**
     * 以lambda方式获取setter对应的属性名，要求对应的setter方法命名为setXXX
     *
     * @param func 可序列化的函数对象
     * @return java.lang.String
     */
    public static <T, U> String getRawPropertyBySetter(SerializableBiConsumer<T, U> func) {
        return getPropertyBySetter(func);
    }

    /**
     * 以lambda方式获取setter对应的属性名，要求对应的setter方法命名为setXXX
     *
     * @param func 可序列化的函数对象
     * @return java.lang.String
     */
    public static <T extends BiConsumer<?, ?>> String getPropertyBySetter(T func) {
        String result = null;
        if (func instanceof Serializable) {
            String setter = getLambdaMethodName((Serializable)func);
            if (null != setter && SET_PATTERN.matcher(setter).matches()) {
                setter = setter.substring(3);
                result = Introspector.decapitalize(setter);
            }
        }

        return result;
    }

    /**
     * 获取函数的方法名
     *
     * @param serializable 可序列化的函数对象
     * @return java.lang.String
     */
    private static String getLambdaMethodName(Serializable serializable) {
        String result = null;
        if (null != serializable) {
            try {
                Method method = serializable.getClass().getDeclaredMethod("writeReplace");
                method.setAccessible(Boolean.TRUE);
                SerializedLambda serializedLambda = (SerializedLambda)method.invoke(serializable);
                result = serializedLambda.getImplMethodName();
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException("Failed to get lambda implement name due to:" + e.getMessage());
            }
        }

        return result;
    }
}