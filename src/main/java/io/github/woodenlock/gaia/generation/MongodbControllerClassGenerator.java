package io.github.woodenlock.gaia.generation;

import io.github.woodenlock.gaia.base.BaseMongodbController;
import io.github.woodenlock.gaia.base.BaseSpringDataController;

/**
 * 默认的Mongodb controller类信息生成器
 *
 * @author woodenlock
 * @date 2021/5/23 17:49
 * @see BaseMongodbController
 */
@SuppressWarnings("unused")
public class MongodbControllerClassGenerator extends BaseSpringControllerClassGenerator {

    @Override
    @SuppressWarnings("rawtypes")
    protected Class<? extends BaseSpringDataController> getSuperClass() {
        return BaseMongodbController.class;
    }
}