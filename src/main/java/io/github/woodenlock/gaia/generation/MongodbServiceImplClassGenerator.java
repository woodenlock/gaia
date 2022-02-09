package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.base.BaseMongodbServiceImpl;
import io.github.woodenlock.gaia.base.BaseSpringDataServiceImpl;
import org.bson.types.ObjectId;

import java.io.Serializable;

/**
 * 默认的spring-data-mongodb ServiceImpl类信息生成器
 * @see BaseMongodbServiceImpl
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 */
@SuppressWarnings("unused")
public class MongodbServiceImplClassGenerator extends BaseSpringDataServiceImplClassGenerator {

    @Override
    Class<? extends Serializable> getKeyType() {
        return ObjectId.class;
    }

    @Override
    @SuppressWarnings("rawtypes")
    Class<? extends BaseSpringDataServiceImpl> getImplBaseType() {
        return BaseMongodbServiceImpl.class;
    }
}