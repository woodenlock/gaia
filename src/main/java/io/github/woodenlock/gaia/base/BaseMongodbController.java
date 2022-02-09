package io.github.woodenlock.gaia.base;

import io.github.woodenlock.gaia.util.DynamicModuleUtils;
import org.bson.types.ObjectId;

import java.io.Serializable;
import java.util.function.Function;

/**
 * mongodb为存储介质的模块控制器基类，默认主键为 {@link ObjectId}
 *
 * @author woodenlock
 * @date 2021-10-17 18:00:06
 */
public abstract class BaseMongodbController<E, V extends Serializable, O, S extends Serializable>
    extends BaseSpringDataController<E, V, O, S, ObjectId, String> {

    @Override
    protected Function<String, ObjectId> convertKey() {
        return id -> DynamicModuleUtils.isBlank(id) ? null : new ObjectId(id.trim());
    }
}