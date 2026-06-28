package com.supermap.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UploadStatus {

    PROCESSING("处理中"),
    SUCCESS("成功"),
    FAILED("失败");

    private final String desc;

}
