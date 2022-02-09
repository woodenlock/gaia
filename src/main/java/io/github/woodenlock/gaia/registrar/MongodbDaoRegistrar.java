package io.github.woodenlock.gaia.registrar;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.support.MongoRepositoryFactoryBean;

/**
 * 默认spring-data-Mongodb DAO注册器实现
 * @see MongoRepository
 * @see MongoRepositoryFactoryBean
 *
 * @author woodenlock
 * @date 2021/5/23 15:45
 */
@SuppressWarnings("unused")
public class MongodbDaoRegistrar extends BaseFactoryBeanRegistrar {

    @Override
    @SuppressWarnings("rawtypes")
    Class<? extends FactoryBean> getBeanType() {
        return MongoRepositoryFactoryBean.class;
    }
}