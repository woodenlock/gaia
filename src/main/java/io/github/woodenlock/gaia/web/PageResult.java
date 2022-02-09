package io.github.woodenlock.gaia.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * 基础分页查询结果对象
 *
 * @author woodenlock
 * @date 2019/03/25 16:44
 */
@SuppressWarnings("unused")
public class PageResult<R> implements Serializable {

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
     * 数据总条数，自然数
     */
    private Long total;

    /**
     * 数据总页数，自然数
     */
    private Long pages;

    /**
     * 数据开始的条数序号，自然数
     */
    private Long start;

    /**
     * 数据结束的条数序号，自然数
     */
    private Long end;

    /**
     * 查询到的业务结果集
     */
    private List<R> list;

    /**
     * 执行分页结果转换
     *
     * @param size    每页大小
     * @param total   总条数
     * @param current 当前页码数
     * @param list    当前查询到的记录集合
     * @param converter    对象转换函数
     * @return PageResult<V>
     */
    public static <E, V> PageResult<V> of(long size, long total, long current, List<E> list, Function<E, V> converter) {
        PageResult<V> result = new PageResult<>();

        long pages, start, end = start = pages = 0;
        if(total <= 0 || size <= 0) {
            current = 1;
        }else {
            pages = total / size + (total % size == 0 ? 0 : 1);
            current = resetCount(current, 1, pages);
            start = (current - 1) * size + 1;
            end = resetCount(current * size, start, total);
        }

        result.setPages(pages);
        result.setSize(size);
        result.setTotal(total);
        result.setCurrent(current);
        result.setStart(start);
        result.setEnd(end);

        List<V> vos = new ArrayList<>();
        if (null != converter && null != list && !list.isEmpty()) {
            list.forEach(re -> vos.add(converter.apply(re)));
        }
        result.setList(vos);

        return result;
    }

    /**
     * 重置数值为具体的某个区间
     * @param origin 原始数值
     * @param min 最小值
     * @param max 最大值
     * @return 区间值
     */
    private static long resetCount(long origin, long min, long max) {
        long result = origin;
        if(result < min) {
            result = min;
        }
        if(result > max) {
            result = max;
        }

        return result;
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

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Long getPages() {
        return pages;
    }

    public void setPages(Long pages) {
        this.pages = pages;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public List<R> getList() {
        return list;
    }

    public void setList(List<R> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "{" + "\"size\":" + size + ",\"current\":" + current + ",\"total\":" + total + ",\"pages\":" + pages
            + ",\"start\":" + start + ",\"end\":" + end + ",\"list\":" + list + "}";
    }
}