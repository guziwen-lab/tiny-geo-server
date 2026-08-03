package com.supermap.task.param;

import com.supermap.AnalysisParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 属性计算分析参数
 * <p>
 * 为输入图层添加一个或多个计算字段（SQL 表达式），生成结果表。
 * 适用于开发区变化情况分析中计算 change_type 字段等场景。
 * <p>
 * jsonArr 参数格式：JSON 数组，
 * 例如 {@code [{"name":"change_type","expression":"CASE WHEN ... END"}]}
 *
 * @author gzw
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttributeCalculateParam implements AnalysisParam {

    /**
     * 计算字段列表
     */
    private List<CalculatedField> fields;

    /**
     * 计算字段定义
     *
     * @param name       字段名（需符合列名规范）
     * @param expression SQL 表达式（如 CASE WHEN ... END）
     */
    public record CalculatedField(String name, String expression) {

    }

}
