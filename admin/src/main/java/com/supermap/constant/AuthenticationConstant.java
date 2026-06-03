package com.supermap.constant;

/**
 * @author gzw
 */
public class AuthenticationConstant {

    public static final String USER_KEY_PREFIX = "sys:user:";

    public static final String LOGIN_RETRY_KEY_PREFIX = "login:retry";

    public static final Long DEFAULT_EXPIRE_SECONDS = 60 * 60 * 24 * 7L * 365;

    public static final String CAPTCHA_KEY_PREFIX = "captcha:";

    public static final Long DEFAULT_CAPTCHA_EXPIRE_SECONDS = 5 * 60L;

}
