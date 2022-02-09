package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.Part;
import org.springframework.data.solr.repository.SolrCrudRepository;
import org.springframework.lang.NonNull;

/**
 * 默认以String为主键的spring-data-solr dao类信息生成器
 * @see SolrCrudRepository
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 */
@SuppressWarnings("unused")
public class SolrDaoClassGenerator implements ClassGenerator {

    @Override
    public Class<?> apply(@NonNull Part part) {
        return SpringDataClassUtils.generateRepository(part.getEntity(), String.class, SolrCrudRepository.class, part.getPath(), part.getSuffix());
    }
}