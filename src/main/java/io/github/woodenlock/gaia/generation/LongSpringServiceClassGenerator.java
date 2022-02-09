package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.Part;
import io.github.woodenlock.gaia.base.BaseSpringDataService;
import org.springframework.lang.NonNull;

/**
 * 默认以Long为主键的spring data service类信息生成器
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 * @see BaseSpringDataService
 */
@SuppressWarnings("unused")
public class LongSpringServiceClassGenerator implements ClassGenerator {

    @Override
    public Class<?> apply(@NonNull Part part) {
        return SpringDataClassUtils.generateServiceInterface(part.getEntity(), Long.class, part.getPath(), part.getSuffix());
    }
}