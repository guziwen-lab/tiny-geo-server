/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 * <p>
 * https://www.renren.io
 * <p>
 * 版权所有，侵权必究！
 */

package com.supermap.exception;

import com.supermap.common.util.MessageUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * 自定义异常
 */
@Setter
@Getter
public class CommonException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    private int code;
    private String msg;

    public CommonException(int code) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public CommonException(int code, String... params) {
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public CommonException(int code, Throwable e) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code);
    }

    public CommonException(int code, Throwable e, String... params) {
        super(e);
        this.code = code;
        this.msg = MessageUtils.getMessage(code, params);
    }

    public CommonException(String msg) {
        super(msg);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

    public CommonException(String msg, Throwable e) {
        super(msg, e);
        this.code = ErrorCode.INTERNAL_SERVER_ERROR;
        this.msg = msg;
    }

}