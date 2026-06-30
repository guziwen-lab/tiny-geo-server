package com.supermap.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author gzw
 */
@Getter
@AllArgsConstructor
public enum TaskStatus {

    NOT_PROCESSED("未处理"),
    PROCESSING("处理中"),
    SUCCESS("成功"),
    FAILED("失败");

    private final String desc;

}
