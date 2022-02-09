package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.ModuleTypeCache;
import io.github.woodenlock.gaia.Part;
import io.github.woodenlock.gaia.common.GenerateConst;
import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import org.springframework.lang.NonNull;

/**
 * 默认的MybatisPlus controller类信息生成器
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 * @see io.github.woodenlock.gaia.base.BaseMyBatisPlusController
 */
@SuppressWarnings("unused")
public class MybatisPlusControllerClassGenerator implements ClassGenerator {

    @Override
    public Class<?> apply(@NonNull Part part) {
        Class<?> entity = part.getEntity();
        String viewClassName = ModuleTypeCache.acquireCanonicalName(entity, GenerateConst.Component.VO);
        if (DynamicModuleUtils.isBlank(viewClassName)) {
            throw new RuntimeException("Failed to generate mybatis controller class due to illegal params:" + part);
        }

        Object config = part.getCustomizes().get(GenerateConst.Config.REQUEST_SUFFIX);
        String requestSuffix = config instanceof String && DynamicModuleUtils.isNotBlank((String)config) ? ((String)config) : null;

        return MybatisPlusClassUtils
            .generateMbpControllerClass(entity, part.getPath(), part.getSuffix(), viewClassName, requestSuffix);
    }
}