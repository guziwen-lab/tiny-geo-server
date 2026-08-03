package com.supermap.task.param;

import com.supermap.AnalysisParam;
import com.supermap.common.util.StringUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
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
@Data
@NoArgsConstructor
public class IntersectSplitParam implements AnalysisParam {

    /**
     * 源表A（第一个图层）待按面积比例拆分的字段名列表
     */
    private List<SplitField> splitFieldsA = new ArrayList<>();

    /**
     * 源表B（第二个图层）待按面积比例拆分的字段名列表
     */
    private List<SplitField> splitFieldsB = new ArrayList<>();

    /** 结果中保存 A 表面积比例的字段名；为空时不输出。 */
    private String ratioFieldA;

    /** 结果中保存 B 表面积比例的字段名；为空时不输出。 */
    private String ratioFieldB;

    public IntersectSplitParam(List<SplitField> splitFieldsA,
                               List<SplitField> splitFieldsB,
                               String ratioFieldA,
                               String ratioFieldB) {
        this.splitFieldsA = splitFieldsA;
        this.splitFieldsB = splitFieldsB;
        this.ratioFieldA = ratioFieldA;
        this.ratioFieldB = ratioFieldB;
    }

    /** 一个源字段及其在结果表中的拆分字段名。 */
    public record SplitField(String sourceField, String resultField) {

        public String resultField() {
            return StringUtils.isEmpty(resultField) ? sourceField + "_split" : resultField;
        }

        public static SplitField withDefaultResult(String sourceField) {
            return new SplitField(sourceField, sourceField + "_split");
        }

    }

}
