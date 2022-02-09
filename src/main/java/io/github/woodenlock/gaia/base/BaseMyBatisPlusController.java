package io.github.woodenlock.gaia.base;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import io.github.woodenlock.gaia.web.PageQuery;
import io.github.woodenlock.gaia.web.PageResult;
import io.github.woodenlock.gaia.web.RestOrder;
import io.github.woodenlock.gaia.web.RestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

/**
 * MBP为存储框架的模块控制器基类
 *
 * @author woodenlock
 * @date 2021-10-17 18:00:06
 */
public abstract class BaseMyBatisPlusController<E, V extends Serializable, O, S extends Serializable>
    extends BaseController<E, V, O, S, Long> {

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    private IService<E> service;

    @Override
    @GetMapping("/{id}")
    public RestResponse<V> query(@PathVariable Long id) {
        return new RestResponse<>(getEntity2ViewConvertor().apply(service.getById(id)));
    }

    @Override
    @PostMapping("/page")
    public RestResponse<PageResult<V>> page(@RequestBody PageQuery<S> page) {
        Page<E> resp =
            service.page(convert(page), new QueryWrapper<>(getSearch2EntityConvertor().apply(page.getSearch())));

        return new RestResponse<>(convert(resp, getEntity2ViewConvertor()));
    }

    @Override
    @PostMapping
    public RestResponse<Boolean> add(@RequestBody O operation) {
        return new RestResponse<>(service.save(getOperation2EntityConvertor().apply(operation)));
    }

    @Override
    @PutMapping
    public RestResponse<Boolean> update(@RequestBody O operation) {
        return new RestResponse<>(service.updateById(getOperation2EntityConvertor().apply(operation)));
    }

    @Override
    @DeleteMapping("/{id}")
    public RestResponse<Boolean> delete(@PathVariable Long id) {
        return new RestResponse<>(service.removeById(id));
    }

    @SuppressWarnings("unused")
    protected IService<E> getService() {
        return service;
    }

    /**
     * 执行分页参数转换
     * 通过构造独立的静态方法来保留泛型约束
     *
     * @param page MBP返回的分页实例
     * @return IPage<E>
     */
    public static <E, S extends Serializable> Page<E> convert(PageQuery<S> page) {
        if (null == page || null == page.getCurrent() || null == page.getSize()) {
            return null;
        }

        Page<E> result = new Page<>(page.getCurrent(), page.getSize());
        List<RestOrder> ovs = page.getOrders();
        if (null != ovs && !ovs.isEmpty()) {
            ovs.forEach(ov -> {
                OrderItem oi = new OrderItem();
                oi.setColumn(ov.getColumn());
                oi.setAsc(ov.getAsc());
                result.orders().add(oi);
            });
        }

        return result;
    }

    /**
     * 执行分页结果转换
     * 通过构造独立的静态方法来保留泛型约束
     *
     * @param page MBP返回的分页实例
     * @param func 对象转换函数
     * @return PageResult<V>
     */
    public static <E, V extends Serializable> PageResult<V> convert(IPage<E> page, Function<E, V> func) {
        return null == page ? null :
            PageResult.of(page.getSize(), page.getTotal(), page.getCurrent(), page.getRecords(), func);
    }
}