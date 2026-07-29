package com.supermap.task.param;

import com.supermap.AnalysisParam;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 属性过滤分析参数
 * <p>
 * subType 参数直接传入 SQL WHERE 子句字符串（不含 WHERE 关键字），
 * 例如 {@code DLBM IN ('0201','0202') AND TBDLMJ_SPLIT > 0.0001}。
 * <p>
 * 该参数由业务编排层内部构建，不直接暴露给前端用户。
 *
 * @author gzw
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterParam implements AnalysisParam {

    /**
     * SQL WHERE 子句（不含 WHERE 关键字）
     */
    private String whereClause;

}
