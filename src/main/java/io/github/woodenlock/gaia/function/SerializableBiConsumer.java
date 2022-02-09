package io.github.woodenlock.gaia.function;

import java.io.Serializable;
import java.util.function.BiConsumer;

/**
 * 可序列化函数式接口
 *
 * @author woodenlock
 * @date 2021/6/24 17:15
 */
@FunctionalInterface
public interface SerializableBiConsumer<T, U> extends BiConsumer<T, U>, Serializable {

}