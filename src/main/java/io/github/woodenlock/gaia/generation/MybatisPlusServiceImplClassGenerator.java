package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.ModuleTypeCache;
import io.github.woodenlock.gaia.Part;
import io.github.woodenlock.gaia.common.GenerateConst;
import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import org.springframework.lang.NonNull;

/**
 * 默认的MybatisPlus ServiceImpl类信息生成器
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 * @see com.baomidou.mybatisplus.extension.service.impl.ServiceImpl
 */
@SuppressWarnings("unused")
public class MybatisPlusServiceImplClassGenerator implements ClassGenerator {

    @Override
    public Class<?> apply(@NonNull Part part) {
        Class<?> entity = part.getEntity();
        String daoClassName = ModuleTypeCache.acquireCanonicalName(entity, GenerateConst.Component.DAO);
        String serviceClassName = ModuleTypeCache.acquireCanonicalName(entity, GenerateConst.Component.SERVICE);
        if (DynamicModuleUtils.isBlank(daoClassName) || DynamicModuleUtils.isBlank(serviceClassName)) {
            throw new RuntimeException("Failed to generate mybatis service impl class due to illegal params:" + part);
        }

        return MybatisPlusClassUtils
            .generateMbpServiceImplClass(entity, part.getPath(), daoClassName, serviceClassName, part.getSuffix());
    }
}