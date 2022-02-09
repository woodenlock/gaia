package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.Part;
import org.springframework.lang.NonNull;

import java.util.function.Function;

/**
 * 动态模块的类生成器
 * 需要存在默认的构造函数
 *
 * @author woodenlock
 * @date 2021/5/23 17:43
 * @see Part#getGenerate()
 */
public interface ClassGenerator extends Function<Part, Class<?>> {

    /**
     * 生成类信息
     *
     * @param part     目标模块的组件封装类
     * @return java.lang.Class<?>
     */
    @Override
    Class<?> apply(@NonNull Part part);
}