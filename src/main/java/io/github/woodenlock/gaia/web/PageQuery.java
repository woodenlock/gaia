package io.github.woodenlock.gaia.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 基础分页查询请求对象
 *
 * @author woodenlock
 * @date 2019/03/25 16:44
 */
@SuppressWarnings("unused")
public class PageQuery<S> implements Serializable {

    private static final long serialVersionUID = 4916282748296022943L;

    /**
     * 每页大小，正整数，默认为10
     */
    private Long size;

    /**
     * 当前页码数，正整数，默认为1
     */
    private Long current;

    /**
     * 排序集合
     */
    private List<RestOrder> orders;

    /**
     * 业务查询对象参数
     */
    private S search;

    public PageQuery() {
        this(10L, 1L);
    }

    public PageQuery(Long size, Long current) {
        this.size = size;
        this.current = current;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getCurrent() {
        return current;
    }

    public void setCurrent(Long current) {
        this.current = current;
    }

    public List<RestOrder> getOrders() {
        return orders;
    }

    public void setOrders(List<RestOrder> orders) {
        this.orders = orders;
    }

    public S getSearch() {
        return search;
    }

    public void setSearch(S search) {
        this.search = search;
    }

    public void addOrder(String column) {
        addOrder(new RestOrder(column));
    }

    public void addOrder(String column, boolean asc) {
        addOrder(new RestOrder(column, asc));
    }

    public void addOrder(RestOrder order) {
        if (null != order) {
            this.orders = null == this.orders ? new ArrayList<>() : this.orders;
            this.orders.add(order);
        }
    }

    @Override
    public String toString() {
        return "{" + "\"size\":" + size + ",\"current\":" + current + ",\"orders\":" + orders + ",\"search\":" + search
            + "}";
    }
}