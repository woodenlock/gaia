package io.github.woodenlock.gaia.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 可序列化函数式接口
 *
 * @author woodenlock
 * @date 2021/6/24 17:15
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {

}