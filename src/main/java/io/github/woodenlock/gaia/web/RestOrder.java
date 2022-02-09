package io.github.woodenlock.gaia.web;

import java.io.Serializable;

/**
 * 基础分页请求排序对象
 *
 * @author woodenlock
 * @date 2021/5/23 20:47
 */
@SuppressWarnings("unused")
public class RestOrder implements Serializable {

    private static final long serialVersionUID = 4916282748296022942L;

    /**
     * 字段名
     */
    private String column;

    /**
     * 是否正序排列，默认 true
     */
    private Boolean asc;

    public RestOrder() {
        this(null);
    }

    public RestOrder(String column) {
        this(column, true);
    }

    public RestOrder(String column, boolean asc) {
        this.column = column;
        this.asc = asc;
    }

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public Boolean getAsc() {
        return asc;
    }

    public void setAsc(Boolean asc) {
        this.asc = asc;
    }

    @Override
    public String toString() {
        return "{" + "\"column\":\"" + column + '\"' + ",\"asc\":" + asc + "}";
    }
}