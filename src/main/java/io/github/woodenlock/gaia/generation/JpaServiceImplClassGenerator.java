package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.base.BaseJpaServiceImpl;
import io.github.woodenlock.gaia.base.BaseSpringDataServiceImpl;

import java.io.Serializable;

/**
 * 默认以Long为主键的spring-data-jpa ServiceImpl类信息生成器
 * @see BaseJpaServiceImpl
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 */
@SuppressWarnings("unused")
public class JpaServiceImplClassGenerator extends BaseSpringDataServiceImplClassGenerator {

    @Override
    Class<? extends Serializable> getKeyType() {
        return Long.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    Class<? extends BaseSpringDataServiceImpl> getImplBaseType() {
        return BaseJpaServiceImpl.class;
    }
}