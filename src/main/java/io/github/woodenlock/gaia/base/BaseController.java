package io.github.woodenlock.gaia.base;

import io.github.woodenlock.gaia.function.SerializableFunction;
import io.github.woodenlock.gaia.util.ReflectUtils;
import io.github.woodenlock.gaia.web.PageQuery;
import io.github.woodenlock.gaia.web.PageResult;
import io.github.woodenlock.gaia.web.RestResponse;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;

/**
 * 最基础的模块控制器接口定义
 *
 * @author woodenlock
 * @date 2021-10-17 18:00:06
 */
public abstract class BaseController<E, V, O, S, ID extends Serializable> {

    private Class<?>[] types = null;

    /**
     * 根据id主键查询
     *
     * @param id id主键
     * @return 结果视图
     */
    @GetMapping("/{id}")
    public abstract RestResponse<V> query(@PathVariable ID id);

    /**
     * 分页查询
     *
     * @param page id主键
     * @return 分页结果
     */
    @PostMapping("/page")
    public abstract RestResponse<PageResult<V>> page(@RequestBody PageQuery<S> page);

    /**
     * 新增
     *
     * @param operation 操作对象
     * @return 操作结果
     */
    @PostMapping
    public abstract RestResponse<Boolean> add(@RequestBody O operation);

    /**
     * 新增
     *
     * @param operation 操作对象
     * @return 操作结果
     */
    @PutMapping
    public abstract RestResponse<Boolean> update(@RequestBody O operation);

    /**
     * 根据id主键删除
     *
     * @param id id主键
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public abstract RestResponse<Boolean> delete(@PathVariable ID id);

    /**
     * 获取实体转视图的默认转换器
     *
     * @return com.tanwin.matrix.server.common.SerializableFunction<E, V>
     */
    @NonNull
    @SuppressWarnings("unchecked")
    protected SerializableFunction<E, V> getEntity2ViewConvertor() {
        return t -> ReflectUtils.conversion(t, (Class<V>)getRawType(1));
    }

    /**
     * 获取操作对象转实体的默认转换器
     *
     * @return com.tanwin.matrix.server.common.SerializableFunction<E, V>
     */
    @NonNull
    @SuppressWarnings("unchecked")
    protected SerializableFunction<O, E> getOperation2EntityConvertor() {
        return t -> ReflectUtils.conversion(t, (Class<E>)getRawType(0));
    }

    /**
     * 获取搜索对象转实体的默认转换器
     *
     * @return com.tanwin.matrix.server.common.SerializableFunction<E, V>
     */
    @NonNull
    @SuppressWarnings("unchecked")
    protected SerializableFunction<S, E> getSearch2EntityConvertor() {
        return t -> ReflectUtils.conversion(t, (Class<E>)getRawType(0));
    }

    /**
     * 获取当前的泛型类型
     *
     * @param index 目标泛型的下标
     * @return java.lang.Class<?>
     */
    private Class<?> getRawType(int index) {
        if (null == types) {
            types = ReflectUtils.getRawTypes(this, BaseController.class);
        }

        return types[index];
    }
}