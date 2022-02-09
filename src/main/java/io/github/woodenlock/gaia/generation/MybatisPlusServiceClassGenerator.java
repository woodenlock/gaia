package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.Part;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.lang.NonNull;

/**
 * 默认的MybatisPlus iService类信息生成器
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 * @see com.baomidou.mybatisplus.extension.service.IService
 */
@SuppressWarnings("unused")
public class MybatisPlusServiceClassGenerator implements ClassGenerator {

    @Override
    public Class<?> apply(@NonNull Part part) {
        return MybatisPlusClassUtils.generateMbpServiceInterface(part.getEntity(), part.getPath(), part.getSuffix());
    }
}