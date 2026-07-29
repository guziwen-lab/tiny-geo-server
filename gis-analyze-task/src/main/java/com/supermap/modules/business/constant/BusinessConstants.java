package com.supermap.modules.business.constant;

import java.util.List;

/**
 * 自然资源监测业务常量
 * <p>
 * 包含其他农用地分析和国家级开发区分析所需的地类编码、要素代码、建设状态等常量。
 *
 * @author gzw
 */
public final class BusinessConstants {

    private BusinessConstants() {
    }

    /**
     * 其他农用地地类编码（非同口径和同口径共用）
     * <p>
     * 来源：2025年自然资源监测-其他农用地图斑分析需求文档
     */
    public static final List<String> QTYD_DLBM = List.of(
            "0201", "0201K", "0202", "0202K", "0203", "0203K", "0204", "0204K",
            "0301", "0301K", "0302", "0302K", "0303", "0304", "0305", "0306",
            "0307", "0307K", "0401", "0402", "0403", "0403K", "0404",
            "1006", "1103", "1104", "1104A", "1104K", "1107", "1107A", "1202", "1203"
    );

    /**
     * 要素代码（其他农用地变化图斑过滤条件）
     * <p>
     * 对应要素名称：明确建设用途的建/构筑物、不明用途的建/构筑物、耕地、园地、林地、
     * 草地、水面、硬化、推堆土、建成道路、在建道路、路网、铁路、水工建筑、采矿、
     * 公园绿地、瓦砾、推平、其他
     */
    public static final List<String> QTYD_YSDM = List.of(
            "20", "JZ", "DL1", "DL2", "DL3", "TL", "SJ", "CK", "LD", "YH", "TD",
            "01", "02", "03", "04", "SM", "WL", "TP", "QT"
    );

    /**
     * 面积阈值（平方米），小于此值的图斑被过滤
     */
    public static final double AREA_THRESHOLD = 0.0001;

    /**
     * 建设用地地类编码（开发区建设密度分析用）
     * <p>
     * 用于从2024年变更调查数据库中过滤建设用地图斑
     */
    public static final List<String> JSYD_DLBM = List.of(
            "05H1", "0508", "0601", "0602", "0603", "0701", "0702",
            "08H1", "08H2", "0809", "0810", "09",
            "1001", "1002", "1003", "1004", "1005", "1007", "1008", "1009",
            "1109", "1201"
    );

    /**
     * 建设状态代码 - 已建成
     */
    public static final String JSZT_YJC = "YJC";

    /**
     * 建设状态代码 - 正在建设
     */
    public static final String JSZT_ZZJS = "ZZJS";

    /**
     * 建设状态代码 - 未建设
     */
    public static final String JSZT_WJS = "WJS";

}
