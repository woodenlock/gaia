package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.ModuleTypeCache;
import io.github.woodenlock.gaia.Part;
import io.github.woodenlock.gaia.base.BaseSpringDataServiceImpl;
import io.github.woodenlock.gaia.common.GenerateConst;
import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import org.springframework.lang.NonNull;

import java.io.Serializable;

/**
 * spring-data相关service实现类生成器的基类
 *
 * @author woodenlock
 * @date 2022/1/21 21:02
 */
public abstract class BaseSpringDataServiceImplClassGenerator implements ClassGenerator {
    /**
     * 获取主键类型
     * @return 主键类型
     */
    abstract Class<? extends Serializable> getKeyType();

    /**
     * 获取实现类基类类型
     * @return 实现类基类类型
     */
    @SuppressWarnings("rawtypes")
    abstract Class<? extends BaseSpringDataServiceImpl> getImplBaseType();

    @Override
    public Class<?> apply(@NonNull Part part) {
        Class<?> entity = part.getEntity();
        String daoClassName = ModuleTypeCache.acquireCanonicalName(entity, GenerateConst.Component.DAO);
        String serviceClassName = ModuleTypeCache.acquireCanonicalName(entity, GenerateConst.Component.SERVICE);
        if (DynamicModuleUtils.isBlank(daoClassName) || DynamicModuleUtils.isBlank(serviceClassName)) {
            throw new RuntimeException("Failed to generate spring data service impl class due to illegal params:" + part);
        }

        return SpringDataClassUtils
            .generateServiceImpl(entity, getImplBaseType(), getKeyType(), part.getPath(), daoClassName, serviceClassName, part.getSuffix());
    }
}