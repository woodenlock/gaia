package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.Part;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.lang.NonNull;

/**
 * 默认以String为主键的spring-data-Elasticsearch dao类信息生成器
 * @see ElasticsearchRepository
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 */
@SuppressWarnings("unused")
public class ElasticSearchDaoClassGenerator implements ClassGenerator {

    @Override
    public Class<?> apply(@NonNull Part part) {
        return SpringDataClassUtils.generateRepository(part.getEntity(), String.class, ElasticsearchRepository.class, part.getPath(), part.getSuffix());
    }
}