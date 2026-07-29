package com.supermap.modules.business.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 统计口径
 * <p>
 * 非同口径使用 DLBM（地类编码）字段过滤，
 * 同口径使用 DLBMTKJ（同口径地类编码）字段过滤。
 * 两者使用的地类编码集合相同。
 *
 * @author gzw
 */
@Getter
@AllArgsConstructor
public enum Caliber {

    NON_TONG_KOU_JING("非同口径"),

    TONG_KOU_JING("同口径");

    private final String desc;

}
