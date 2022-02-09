package io.github.woodenlock.gaia.registrar;

import io.github.woodenlock.gaia.base.JpaSpecificationRepository;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactoryBean;

/**
 * 默认spring-data-jpa DAO注册器实现
 * @see JpaRepository
 * @see JpaSpecificationRepository
 * @see JpaRepositoryFactoryBean
 *
 * @author woodenlock
 * @date 2021/5/23 15:45
 */
@SuppressWarnings("unused")
public class JpaDaoRegistrar extends BaseFactoryBeanRegistrar {

    @Override
    @SuppressWarnings("rawtypes")
    Class<? extends FactoryBean> getBeanType() {
        return JpaRepositoryFactoryBean.class;
    }
}