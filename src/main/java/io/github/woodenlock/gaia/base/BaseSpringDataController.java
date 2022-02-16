package io.github.woodenlock.gaia.base;

import io.github.woodenlock.gaia.web.PageQuery;
import io.github.woodenlock.gaia.web.PageResult;
import io.github.woodenlock.gaia.web.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 基础的spring-data控制器
 *
 * @author woodenlock
 * @date 2021-10-17 18:00:06
 */
public abstract class BaseSpringDataController<E, V, O, S, K extends Serializable, ID extends Serializable>
    extends BaseController<E, V, O, S, ID> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private BaseSpringDataService<E, K> service;

    @Override
    @GetMapping("/{id}")
    public RestResponse<V> query(@PathVariable ID id) {
        return new RestResponse<>(getEntity2ViewConvertor().apply(getService().getById(convertKey().apply(id))));
    }

    @Override
    @DeleteMapping("/{id}")
    public RestResponse<Boolean> delete(@PathVariable ID id) {
        return new RestResponse<>(getService().deleteById(convertKey().apply(id)));
    }

    @Override
    @PostMapping("/page")
    public RestResponse<PageResult<V>> page(@RequestBody PageQuery<S> page) {
        Page<E> resp = service.selectPage(getSearch2EntityConvertor().apply(page.getSearch()), page.toPageable());

        return new RestResponse<>(PageResult.of(resp, getEntity2ViewConvertor()));
    }

    @Override
    @PostMapping
    public RestResponse<Boolean> add(@RequestBody O operation) {
        return new RestResponse<>(service.insert(getOperation2EntityConvertor().apply(operation)));
    }

    @Override
    @PutMapping
    public RestResponse<Boolean> update(@RequestBody O operation) {
        return new RestResponse<>(service.save(getOperation2EntityConvertor().apply(operation)));
    }

    /**
     * 获取对应的service实例
     *
     * @return service实例
     */
    @SuppressWarnings("unused")
    protected BaseSpringDataService<E, K> getService() {
        return service;
    }

    /**
     * 对外的主键转换为持久化映射对象的主键
     * @return java.util.function.Function<ID,K>
     */
    protected abstract Function<ID, K> convertKey();
}