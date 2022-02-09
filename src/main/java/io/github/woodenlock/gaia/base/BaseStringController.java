package io.github.woodenlock.gaia.base;

import java.util.function.Function;

/**
 * 基于String为请求主键的spring-data控制器基类
 * 适用于spring-boot-starter-data-solr、spring-boot-starter-data-elasticsearch
 *
 * @author woodenlock
 * @date 2021-10-17 18:00:06
 */
public abstract class BaseStringController<E, V, O, S> extends BaseSpringDataController<E, V, O, S, String, String> {

    @Override
    protected Function<String, String> convertKey() {
        return id -> id;
    }
}