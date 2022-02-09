package io.github.woodenlock.gaia.util;

import org.springframework.aop.framework.AdvisedSupport;
import org.springframework.aop.framework.AopProxy;
import org.springframework.aop.support.AopUtils;

import java.lang.reflect.Field;

/**
 * 从spring容器中获取真实被代理对象工具
 * 基于spring-aop框架的取巧设计，谨慎使用
 * 详见{@link org.springframework.aop.framework.AopProxy}及其实现类
 *
 * @author woodenlock
 * @date 2020/7/24 20:25
 */
@SuppressWarnings("unused")
public enum SpringAopUtils {
    ;

    /**
     * 获取 目标对象
     *
     * @param proxy 代理对象
     * @return 目标对象
     */
    public static Object getTarget(Object proxy) throws Exception {
        if (AopUtils.isAopProxy(proxy)) {
            proxy = AopUtils.isJdkDynamicProxy(proxy) ? getJdkDynamicProxyTargetObject(proxy) :
                getCglibProxyTargetObject(proxy);
            proxy = getTarget(proxy);
        }

        return proxy;
    }

    private static Object getCglibProxyTargetObject(Object proxy) throws Exception {
        // 这几个魔鬼字符不好消除
        Field h = proxy.getClass().getDeclaredField("CGLIB$CALLBACK_0");
        h.setAccessible(true);
        Object dynamicAdvisedInterceptor = h.get(proxy);
        Field advised = dynamicAdvisedInterceptor.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport)advised.get(dynamicAdvisedInterceptor)).getTargetSource().getTarget();
    }

    private static Object getJdkDynamicProxyTargetObject(Object proxy) throws Exception {
        Field h = proxy.getClass().getSuperclass().getDeclaredField("h");
        h.setAccessible(true);
        AopProxy aopProxy = (AopProxy)h.get(proxy);
        Field advised = aopProxy.getClass().getDeclaredField("advised");
        advised.setAccessible(true);
        return ((AdvisedSupport)advised.get(aopProxy)).getTargetSource().getTarget();
    }
}