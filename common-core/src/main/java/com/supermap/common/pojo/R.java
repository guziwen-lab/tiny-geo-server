package com.supermap.common.pojo;

import com.supermap.common.enumeration.BizCodeEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author gzw
 */
@Schema(title = "通用响应对象")
@Data
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final Integer SUCCESS_CODE = 0;

    private static final String SUCCESS_MSG = "success";

    private static final Integer ERROR_CODE = 10000;

    private static final String ERROR_MSG = "系统未知异常";

    @Schema(title = "响应码")
    private Integer code;

    @Schema(title = "响应信息")
    private String msg;

    @Schema(title = "响应数据")
    private T data;

    public R() {
        code = SUCCESS_CODE;
        msg = SUCCESS_MSG;
    }

    public R(Integer code) {
        this.code = code;
    }

    public R(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(Integer code, String msg, T data) {
        this.code = code;
        this.data = data;
        this.msg = msg;
    }

    public static <T> R<T> ok() {
        return new R<>(SUCCESS_CODE, SUCCESS_MSG);
    }

    public static <T> R<T> ok(String msg, T data) {
        return new R<>(SUCCESS_CODE, msg, data);
    }

    public static <T> R<T> ok(T data) {
        return ok(SUCCESS_MSG, data);
    }

    public static <T> R<T> error() {
        return new R<>(ERROR_CODE, ERROR_MSG);
    }

    public static <T> R<T> error(Integer code, String msg, T data) {
        return new R<>(code, msg, data);
    }

    public static <T> R<T> error(Integer code, String msg) {
        return error(code, msg, null);
    }

    public static <T> R<T> error(String message) {
        return error(ERROR_CODE, message);
    }

    public static <T> R<T> error(BizCodeEnum bizCodeEnum) {
        return error(bizCodeEnum.getCode(), bizCodeEnum.getMessage());
    }

    public static <T> R<T> error(BizCodeEnum bizCodeEnum, T data) {
        return new R<>(bizCodeEnum.getCode(), bizCodeEnum.getMessage(), data);
    }

    public boolean check() {
        return Objects.equals(getCode(), SUCCESS_CODE);
    }

}
