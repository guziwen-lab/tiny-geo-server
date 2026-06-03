package com.supermap.common.enumeration;

import lombok.Getter;

/**
 * @author gzw
 */
public class TypeEnum {

    /**
     * 地区类型
     */
    @Getter
    public enum DistrictType {
        /**
         * 国家
         */
        COUNTRY,

        /**
         * 省
         */
        PROVINCE,

        /**
         * 市
         */
        CITY,

        /**
         * 区县
         */
        COUNTY

    }

    /**
     * 地区类型
     */
    @Getter
    public enum AreaUnitType {
        /**
         * 公顷
         */
        HECTARE,

        /**
         * 万公顷
         */
        TEN_THOUSAND_HECTARES,

    }

}
