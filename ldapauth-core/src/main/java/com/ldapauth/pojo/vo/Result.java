package com.ldapauth.pojo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;

import java.io.Serializable;

@Schema(title = "Result", description = "通用返回对象")
public class Result<T>   implements Serializable {

    /**
     * 成功标志
     */
    @Schema(name = "success", description = "成功标志")
    private boolean success = true;

    @Schema(name = "code", description = "状态码")
    private Integer code;

    @Schema(name = "message", description = "提示信息")
    private String message;

    @Schema(name = "data", description = "数据对象")
    private T data;

    public Result(int i, String message, T o) {
        this.code = i;
        this.success = HttpStatus.OK.value() == code ? true : false;
        this.message = message;
        this.data = o;
    }

    public Result() {
        super();
    }

    /**
     * 成功返回结果
     * 0
     *
     * @param data 获取的数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<T>(HttpStatus.OK.value(), HttpStatus.OK.getReasonPhrase(), data);
    }

    /**
     * 失败返回结果
     * custom message
     *
     * @param message 提示信息
     */
    public static <T> Result<T> failed(int code, String message) {
        return new Result<T>(code, message, null);
    }

    /**
     * 失败返回结果
     * custom message
     *
     * @param message 提示信息
     */
    public static <T> Result<T> failed(String message) {
//        return new Result<T>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
        return new Result<T>(HttpStatus.BAD_REQUEST.value(), message, null);
    }

    /**
     * 失败返回结果
     * 400
     *
     * @param message 提示信息
     */
    public static <T> Result<T> failedNetError(String message) {
        return new Result<T>(HttpStatus.INTERNAL_SERVER_ERROR.value(), message, null);
    }

    /**
     * 失败返回结果
     * 404
     */
    public static <T> Result<T> failedNotFound() {
        return new Result<T>(HttpStatus.NOT_FOUND.value(), HttpStatus.NOT_FOUND.getReasonPhrase(), null);
    }

    /**
     * 失败返回结果
     * 403
     */
    public static <T> Result<T> failedForbidden() {
        return new Result<T>(HttpStatus.FORBIDDEN.value(), HttpStatus.FORBIDDEN.getReasonPhrase(), null);
    }


    /**
     * 异常返回结果
     * code
     * message
     *
     * @param message 提示信息
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<T>(code, message, null);
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

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

    public boolean isSuccess() {
        return success;
    }

}
