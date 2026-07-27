package com.supermap.task.param;

import com.supermap.AnalysisParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 相交面积拆分分析参数
 * <p>
 * subType 参数格式为简单字符串：{@code A表字段|B表字段}，多个字段用逗号分隔。
 * 例如 {@code jcmj|tbmj,kcmj,tbdlmj} 表示拆分A表的jcmj字段，
 * 拆分B表的tbmj、kcmj、tbdlmj字段。
 *
 * @author gzw
 */
@AllArgsConstructor
@Data
@NoArgsConstructor
public class IntersectSplitParam implements AnalysisParam {

    /**
     * 源表A（第一个图层）待按面积比例拆分的字段名列表
     */
    private List<String> splitFieldsA;

    /**
     * 源表B（第二个图层）待按面积比例拆分的字段名列表
     */
    private List<String> splitFieldsB;

}
