package io.github.woodenlock.gaia.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态模块的生效注解，启用之后才会被视为有效的动态模块
 *
 * @author woodenlock
 * @date 2021/5/23 15:46
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface DynamicModule {

    /**
     * 是否启用
     **/
    boolean enable() default true;

    /**
     * 持久层方案
     **/
    String value() default "";
}