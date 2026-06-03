package com.supermap.common.util;

import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author gzw
 */
public class NumberUtils extends NumberUtil {

    /**
     * 保留两位小数
     */
    public static BigDecimal roundTwo(BigDecimal value) {
        return round(value, 2);
    }

    /**
     * 计算百分比保留两位小数
     *
     * @param numerator   分子
     * @param denominator 分母
     * @return 结果
     */
    public static BigDecimal roundPercent(BigDecimal numerator, BigDecimal denominator) {
        return new BigDecimal("100").multiply(numerator).divide(denominator, 2, RoundingMode.HALF_UP);
    }

    /**
     * 亩转万亩
     * 把数字除10000，保留两位小数
     */
    public static BigDecimal muToTenThousandMu(BigDecimal fieldValue) {
        if (fieldValue == null) {
            return BigDecimal.ZERO;
        }
        return fieldValue
                .divide(new BigDecimal(10000), 2, RoundingMode.HALF_UP);
    }

    public static BigDecimal getBigDecimalByStr(String s) {
        BigDecimal bigDecimal;
        try {
            bigDecimal = new BigDecimal(s);
        } catch (Exception e) {
            bigDecimal = BigDecimal.ZERO;
        }

        return bigDecimal;
    }

}
