package io.github.woodenlock.gaia.web;

import io.github.woodenlock.gaia.common.Conclusive;

/**
 * 服务结果枚举
 *
 * @author woodenlock
 * @date 2021/12/11 16:41
 */
enum ServiceResults implements Conclusive {
    /**
     * 成功
     **/
    SUCCESS(0, "成功"),

    /**
     * 失败
     **/
    FAILURE(1, "失败"),

    ;

    /**
     * 业务码
     */
    private final Integer code;

    /**
     * 消息
     */
    private final String message;

    ServiceResults(int code, String message) {
        this.code = code;
        this.message = message;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @SuppressWarnings("unused")
    public static ServiceResults resolve(Integer code) {
        if (null != code) {
            for (ServiceResults result : ServiceResults.values()) {
                if (code.equals(result.code)) {
                    return result;
                }
            }
        }

        return null;
    }
}