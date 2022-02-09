package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.Part;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.lang.NonNull;

/**
 * 默认的MongoDB DAO类信息生成器
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 * @see MongoRepository
 */
@SuppressWarnings("unused")
public class MongodbDaoClassGenerator implements ClassGenerator {

    @Override
    public Class<?> apply(@NonNull Part part) {
        return SpringDataClassUtils.generateMongoRepository(part.getEntity(), part.getPath(), part.getSuffix());
    }
}