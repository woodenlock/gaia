package io.github.woodenlock.gaia.registrar;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.data.solr.repository.support.SolrRepositoryFactoryBean;

/**
 * 默认spring-data-solr DAO注册器实现
 * @see SolrCrudRepository
 * @see SolrRepositoryFactoryBean
 *
 * @author woodenlock
 * @date 2021/5/23 15:45
 */
@SuppressWarnings("unused")
public class SolrDaoRegistrar extends BaseFactoryBeanRegistrar {

    @Override
    @SuppressWarnings("rawtypes")
    Class<? extends FactoryBean> getBeanType() {
        return SolrRepositoryFactoryBean.class;
    }
}