package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.Part;
import org.springframework.lang.NonNull;

/**
 * 默认的MybatisPlus DAO类信息生成器
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 * @see com.baomidou.mybatisplus.core.mapper.BaseMapper
 */
@SuppressWarnings("unused")
public class MybatisPlusDaoClassGenerator implements ClassGenerator {

    @Override
    public Class<?> apply(@NonNull Part part) {
        return MybatisPlusClassUtils.generateMapperClass(part.getEntity(), part.getPath(), part.getSuffix());
    }
}