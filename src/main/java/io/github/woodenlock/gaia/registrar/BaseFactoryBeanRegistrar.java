package io.github.woodenlock.gaia.registrar;

import io.github.woodenlock.gaia.util.NamingUtils;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.NonNull;

/**
 * 默认的以spring {@link FactoryBean}方式来注入的bean注册器实现
 * @see FactoryBean
 *
 * @author woodenlock
 * @date 2021/5/23 15:45
 */
public abstract class BaseFactoryBeanRegistrar implements BeanRegistrar {

    @Override
    public String apply(@NonNull BeanDefinitionRegistry registry, @NonNull Class<?> type) {
        AbstractBeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(type).getBeanDefinition();
        definition.setScope(BeanDefinition.SCOPE_SINGLETON);
        definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
        String beanClassName = definition.getBeanClassName();
        if (null != beanClassName) {
            definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName);
            definition.setAttribute(FactoryBean.OBJECT_TYPE_ATTRIBUTE, beanClassName);
        }

        definition.setBeanClass(getBeanType());
        String id = NamingUtils.convertFirst(type.getSimpleName(), false);
        registry.registerBeanDefinition(id, definition);

        return id;
    }

    /**
     * 工厂类类型
     * @return Class
     */
    @SuppressWarnings("rawtypes")
    abstract Class<? extends FactoryBean> getBeanType();
}