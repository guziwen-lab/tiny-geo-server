package com.supermap.common.enumeration;

import lombok.Getter;

/**
 * 错误码和错误信息定义类
 * 1. 错误码定义规则为5位数字
 * 2. 前两位表示业务场景，最后三位表示错误码。例如：10001。10：通用 001：系统未知异常
 * <p>
 * 错误码列表：
 * 10：通用
 * &nbsp;     001：参数格式校验异常
 * &nbsp;     002：请求短信验证码频率过高
 */
@Getter
public enum BizCodeEnum {
    UNKNOWN_EXCEPTION(10000, "系统未知异常"),
    VALID_PARAM_EXCEPTION(10001, "请求参数校验失败"),
    AUTHENTICATION_FAILED(10002, "认证失败"),
    PERMISSIONS_DENIAL(10003, "权限不足"),
    TO_MANY_REQUEST(10004, "请求流量过大"),
    UNAUTHORIZED(10005, "未认证"),
    CAPTCHA_ERROR(10006, "验证码错误"),
    EXCESSIVE_ATTEMPTS(10007, "密码错误次数过多"),

    GEOSPATIAL_DATA_FAILED(20000, "空间数据校验失败"),

    DUPLICATE_KEY_EXCEPTION(30000, "违反唯一约束"),
    ;

    private final int code;
    private final String message;

    BizCodeEnum(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
