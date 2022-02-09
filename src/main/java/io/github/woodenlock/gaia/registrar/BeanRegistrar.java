package io.github.woodenlock.gaia.registrar;

import io.github.woodenlock.gaia.Part;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.NonNull;

import java.util.function.BiFunction;

/**
 * 动态模块的spring注册器
 * 需要存在默认的构造函数
 *
 * @author woodenlock
 * @date 2021/5/23 15:41
 * @see Part#getRegister()
 */
public interface BeanRegistrar extends BiFunction<BeanDefinitionRegistry, Class<?>, String> {

    /**
     * 执行注册
     *
     * @param registry spring容器的注册中心
     * @param type     注册的目标类
     * @return bean注册到spring容器中的id
     */
    @Override
    String apply(@NonNull BeanDefinitionRegistry registry, @NonNull Class<?> type);
}