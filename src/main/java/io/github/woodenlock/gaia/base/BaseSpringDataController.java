package io.github.woodenlock.gaia.base;

import io.github.woodenlock.gaia.web.PageQuery;
import io.github.woodenlock.gaia.web.PageResult;
import io.github.woodenlock.gaia.web.RestOrder;
import io.github.woodenlock.gaia.web.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        Page<E> resp = service.selectPage(getSearch2EntityConvertor().apply(page.getSearch()), convertPageable(page));

        return new RestResponse<>(convert(resp, getEntity2ViewConvertor()));
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
     * 执行分页参数转换
     * 通过构造独立的静态方法来保留泛型约束
     *
     * @param page MBP返回的分页实例
     * @return IPage<E>
     */
    protected Pageable convertPageable(PageQuery<S> page) {
        if (null == page || null == page.getCurrent() || null == page.getSize()) {
            return null;
        }
        List<RestOrder> ovs = page.getOrders();
        Sort sort = Sort.unsorted();
        if (null != ovs && !ovs.isEmpty()) {
            sort = Sort.by(ovs.stream().map(
                ov -> new Sort.Order(null != ov.getAsc() && ov.getAsc() ? Sort.Direction.ASC : Sort.Direction.DESC,
                    ov.getColumn())).collect(Collectors.toList()));
        }

        return PageRequest.of(page.getCurrent().intValue() - 1, page.getSize().intValue(), sort);
    }

    /**
     * 对外的主键转换为持久化映射对象的主键
     * @return java.util.function.Function<ID,K>
     */
    protected abstract Function<ID, K> convertKey();

    /**
     * 执行分页结果转换
     * 通过构造独立的静态方法来保留泛型约束
     *
     * @param page spring data返回的分页实例
     * @param func 对象转换函数
     * @return PageResult<V>
     */
    protected PageResult<V> convert(Page<E> page, Function<E, V> func) {
        if (null == page) {
            return null;
        }

        return PageResult.of(page.getSize(), page.getTotalElements(), page.getNumber(), page.toList(), func);
    }
}