package com.fafu.learning.common;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一API响应结果封装
 */
@Data
public class ApiResult<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 响应码
     */
    private Integer code;
    
    /**
     * 响应数据
     */
    private T data;
    
    /**
     * 响应消息
     */
    private String msg;
    
    public ApiResult() {}
    
    public ApiResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
    
    public ApiResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    
    /**
     * 成功响应
     */
    public static <T> ApiResult<T> success() {
        return new ApiResult<>(200, "success");
    }
    
    /**
     * 成功响应带数据
     */
    public static <T> ApiResult<T> success(T data) {
        return new ApiResult<>(200, "success", data);
    }
    
    /**
     * 成功响应带消息和数据
     */
    public static <T> ApiResult<T> success(String msg, T data) {
        return new ApiResult<>(200, msg, data);
    }
    
    /**
     * 失败响应
     */
    public static <T> ApiResult<T> fail(String msg) {
        return new ApiResult<>(500, msg);
    }
    
    /**
     * 失败响应带状态码
     */
    public static <T> ApiResult<T> fail(Integer code, String msg) {
        return new ApiResult<>(code, msg);
    }
    
    /**
     * 参数错误
     */
    public static <T> ApiResult<T> badRequest(String msg) {
        return new ApiResult<>(400, msg);
    }
    
    /**
     * 未授权
     */
    public static <T> ApiResult<T> unauthorized(String msg) {
        return new ApiResult<>(401, msg);
    }
    
    /**
     * 权限不足
     */
    public static <T> ApiResult<T> forbidden(String msg) {
        return new ApiResult<>(403, msg);
    }
    
    /**
     * 资源不存在
     */
    public static <T> ApiResult<T> notFound(String msg) {
        return new ApiResult<>(404, msg);
    }
}