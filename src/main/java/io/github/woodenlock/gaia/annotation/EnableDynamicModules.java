package io.github.woodenlock.gaia.annotation;

import io.github.woodenlock.gaia.DynamicModuleRegistrar;
import org.springframework.context.annotation.Import;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 启用静态模块功能
 *
 * @author woodenlock
 * @date 2021/12/5 19:52
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(DynamicModuleRegistrar.class)
public @interface EnableDynamicModules {

    /**
     * 是否启用
     **/
    boolean enable() default true;

    /**
     * 包含的实体类集合(类不可重名)，优先于配置文件
     **/
    String[] includes() default {};

    /**
     * 排除的实体类集合，优先于配置文件
     **/
    String[] excludes() default {};
}