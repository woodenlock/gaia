package io.github.woodenlock.gaia.web;

import io.github.woodenlock.gaia.common.Conclusive;

import java.io.Serializable;

/**
 * rest风格 基础http接口响应对象，提供灵活的构造函数
 *
 * @author zhangpeijun
 * @version [v0.0.1, 2016年7月12日]
 */
@SuppressWarnings("unused")
public class RestResponse<T> implements Serializable, Conclusive {

    /**
     * 注释内容
     */
    private static final long serialVersionUID = 1L;

    /**
     * 操作状态码
     */
    private Integer code;

    /**
     * 描述文本
     */
    private String message;

    /**
     * 返回数据值：
     */
    private T data;

    public RestResponse() {
        this(ServiceResults.SUCCESS);
    }

    public RestResponse(Conclusive result) {
        this(result.getCode(), result.getMessage());
    }

    public RestResponse(Integer code, String message) {
        this(code, message, null);
    }

    public RestResponse(T data) {
        this(ServiceResults.SUCCESS, data);
    }

    public RestResponse(Conclusive result, T data) {
        this(result.getCode(), result.getMessage(), data);
    }

    public RestResponse(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> RestResponse<T> success() {
        return new RestResponse<>();
    }

    public static <T> RestResponse<T> success(T data) {
        return new RestResponse<>(data);
    }

    public static <T> RestResponse<T> failure() {
        return new RestResponse<>(ServiceResults.FAILURE);
    }

    public static <T> RestResponse<T> failure(T data) {
        return new RestResponse<>(ServiceResults.FAILURE, data);
    }

    public RestResponse<T> changeFailure(T data) {
        this.data = data;
        return changeFailure();
    }

    public RestResponse<T> changeFailure() {
        return change(ServiceResults.FAILURE);
    }

    public RestResponse<T> changeSuccess(T data) {
        this.data = data;
        return changeSuccess();
    }

    public RestResponse<T> changeSuccess() {
        return change(ServiceResults.SUCCESS);
    }

    public RestResponse<T> change(Conclusive result) {
        this.code = result.getCode();
        this.message = result.getMessage();
        return this;
    }

    public RestResponse<T> code(int code) {
        this.code = code;
        return this;
    }

    public RestResponse<T> message(String message) {
        this.message = message;
        return this;
    }

    public RestResponse<T> data(T data) {
        this.data = data;
        return this;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "{\"code\":" + code + ",\"message\":" + message + ",\"data\":" + data + "}";
    }
}