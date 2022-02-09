package io.github.woodenlock.gaia.registrar;

import io.github.woodenlock.gaia.util.NamingUtils;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.NonNull;

/**
 * 默认的spring bean注册器实现
 * @author woodenlock
 * @date 2021/5/23 15:45
 */
@SuppressWarnings("unused")
public class DefaultBeanRegistrar implements BeanRegistrar {

    @Override
    public String apply(@NonNull BeanDefinitionRegistry registry, @NonNull Class<?> type) {
        String id = NamingUtils.convertFirst(type.getSimpleName(), false);
        AbstractBeanDefinition definition = BeanDefinitionBuilder.genericBeanDefinition(type).getBeanDefinition();
        registry.registerBeanDefinition(id, definition);
        return id;
    }
}