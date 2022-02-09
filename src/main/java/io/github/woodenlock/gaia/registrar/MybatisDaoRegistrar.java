package io.github.woodenlock.gaia.registrar;

import org.mybatis.spring.mapper.MapperFactoryBean;
import org.springframework.beans.factory.FactoryBean;

/**
 * 默认Mybatis DAO注册器实现
 * @see com.baomidou.mybatisplus.core.mapper.BaseMapper
 * @see org.mybatis.spring.mapper.MapperFactoryBean
 *
 * @author woodenlock
 * @date 2021/5/23 15:45
 */
@SuppressWarnings("unused")
public class MybatisDaoRegistrar extends BaseFactoryBeanRegistrar {

    @Override
    @SuppressWarnings("rawtypes")
    Class<? extends FactoryBean> getBeanType() {
        return MapperFactoryBean.class;
    }
}