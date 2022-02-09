package io.github.woodenlock.gaia.common;

/**
 * 通用结果接口
 *
 * @author woodenlock
 * @date 2019/8/25 10:30
 */
public interface Conclusive extends Describable {

    /**
     * 獲取狀態值
     *
     * @return java.lang.Integer
     */
    Integer getCode();
}