package io.github.woodenlock.gaia.registrar;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.elasticsearch.repository.support.ElasticsearchRepositoryFactoryBean;

/**
 * 默认spring-data-Elasticsearch DAO注册器实现
 * @see ElasticsearchRepository
 * @see ElasticsearchRepositoryFactoryBean
 *
 * @author woodenlock
 * @date 2021/5/23 15:45
 */
@SuppressWarnings("unused")
public class ElasticSearchDaoRegistrar extends BaseFactoryBeanRegistrar {

    @Override
    @SuppressWarnings("rawtypes")
    Class<? extends FactoryBean> getBeanType() {
        return ElasticsearchRepositoryFactoryBean.class;
    }
}