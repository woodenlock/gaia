package io.github.woodenlock.gaia.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态模块的组件生成
 *
 * @author woodenlock
 * @date 2021/5/23 15:46
 */
@SuppressWarnings("unused")
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Repeatable(GenerateComponents.class)
public @interface GenerateComponent {

    /**
     * 指定的组件类型
     */
    String type();

    /**
     * 生成类后缀
     *
     * @see io.github.woodenlock.gaia.Part#getSuffix()
     */
    String suffix() default "";

    /**
     * 声明所在包路径，以“/”结尾
     *
     * @see io.github.woodenlock.gaia.Part#getPath()
     */
    String path() default "/";

    /**
     * 是否要生成类信息
     *
     * @see io.github.woodenlock.gaia.Part#getGenerate()
     */
    boolean generated() default true;

    /**
     * 是否要交由spring管理
     *
     * @see io.github.woodenlock.gaia.Part#getRegister()
     */
    boolean registered() default false;

    /**
     * 继承的类、接口
     *
     * @see io.github.woodenlock.gaia.Part#getGeneratorPath()
     */
    String generatorPath() default "";

    /**
     * 向spring容器中注册时使用的注册器实现类,当组件需要交由spring容器管理时使用
     *
     * @see io.github.woodenlock.gaia.Part#getRegistrarPath()
     */
    String registrarPath() default "";
}