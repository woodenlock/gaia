package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.Part;
import io.github.woodenlock.gaia.base.JpaSpecificationRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

/**
 * 默认以Long为主键的spring-data-jpa dao类信息生成器
 * @see JpaRepository
 * @see JpaSpecificationRepository
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 */
@SuppressWarnings("unused")
public class JpaDaoClassGenerator implements ClassGenerator {

    @Override
    public Class<?> apply(@NonNull Part part) {
        return SpringDataClassUtils.generateRepository(part.getEntity(), Long.class,
            JpaSpecificationRepository.class, part.getPath(), part.getSuffix());
    }
}