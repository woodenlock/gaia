package io.github.woodenlock.gaia.base;

import java.util.function.Function;

/**
 * 基于Long为请求主键的spring-data控制器基类
 * 适用于spring-boot-starter-data-jpa
 *
 * @author woodenlock
 * @date 2021-10-17 18:00:06
 */
public abstract class BaseLongController<E, V, O, S> extends BaseSpringDataController<E, V, O, S, Long, Long> {

    @Override
    protected Function<Long, Long> convertKey() {
        return id -> id;
    }
}