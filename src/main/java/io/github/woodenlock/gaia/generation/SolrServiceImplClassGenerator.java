package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.base.BaseSolrServiceImpl;
import io.github.woodenlock.gaia.base.BaseSpringDataServiceImpl;

import java.io.Serializable;

/**
 * 默认以String为主键的spring-data-solr ServiceImpl类信息生成器
 * @see BaseSolrServiceImpl
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 */
@SuppressWarnings("unused")
public class SolrServiceImplClassGenerator extends BaseSpringDataServiceImplClassGenerator {

    @Override
    Class<? extends Serializable> getKeyType() {
        return String.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    Class<? extends BaseSpringDataServiceImpl> getImplBaseType() {
        return BaseSolrServiceImpl.class;
    }
}