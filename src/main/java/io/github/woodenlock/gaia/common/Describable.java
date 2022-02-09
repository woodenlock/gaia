package io.github.woodenlock.gaia.common;

/**
 * 消息描述接口，建议用于系统间交互消息
 *
 * @author woodenlock
 * @date 2019/8/25 10:29
 */
public interface Describable {

    /**
     * 獲取描述信息
     *
     * @return java.lang.String
     */
    String getMessage();
}