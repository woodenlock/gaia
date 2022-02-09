package io.github.woodenlock.gaia.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态模块的组件生成集合
 *
 * @author woodenlock
 * @date 2021/5/23 15:46
 */
@SuppressWarnings("unused")
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateComponents {

    GenerateComponent[] value();
}