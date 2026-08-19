package com.supermap.analyze.task.impl;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.AnalysisResult;
import com.supermap.analyze.AnalysisStep;
import com.supermap.analyze.LayerInfo;
import com.supermap.analyze.service.impl.FilterExecuteService;
import com.supermap.core.util.StringUtils;
import com.supermap.analyze.enums.AnalysisType;
import com.supermap.gis.enums.GeomType;
import com.supermap.analyze.task.AbstractAnalysisTask;
import com.supermap.analyze.task.param.FilterParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 属性过滤分析任务
 * <p>
 * 从输入图层中按 SQL WHERE 条件提取子集，生成结果表。
 * 适用于自然资源监测中过滤建设用地、提取其他农用地变化图斑等场景。
 * <p>
 * whereClause 参数格式：直接传入 WHERE 子句字符串（不含 WHERE 关键字），
 * 例如 {@code DLBM IN ('0201','0202') AND TBDLMJ_SPLIT > 0.0001}。
 *
 * @author gzw
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class FilterAnalysisTask extends AbstractAnalysisTask<FilterParam> {

    private final FilterExecuteService filterExecuteService;

    @Override
    public AnalysisType getType() {
        return AnalysisType.FILTER;
    }

    /**
     * 属性过滤：保持输入几何类型
     *
     * @param context 分析上下文
     * @return 输入几何类型
     */
    @Override
    public GeomType resultGeomType(AnalysisContext<FilterParam> context) {
        List<LayerInfo> layerInfos = context.getInputLayers();
        return layerInfos.get(0).getGeomType();
    }

    @Override
    public FilterParam buildParam(String whereClause) {
        return new FilterParam(whereClause);
    }

    @Override
    protected void validate(AnalysisContext<FilterParam> context) {
        if (context.getParam() == null)
            throw new IllegalArgumentException("分析参数不能为空");

        List<LayerInfo> layers = context.getInputLayers();
        if (layers == null || layers.size() != 1) {
            throw new IllegalArgumentException("属性过滤分析需要且仅需要1个图层");
        }
        if (StringUtils.isEmpty(context.getParam().getWhereClause())) {
            throw new IllegalArgumentException("WHERE 条件不能为空");
        }
    }

    @Override
    protected AnalysisResult doExecute(AnalysisContext<FilterParam> context) {
        LayerInfo input = context.getInputLayers().get(0);
        String newTableName = context.getResultTableName();

        filterExecuteService.execute(input, null, newTableName, context);

        context.addStep(new AnalysisStep(1,
                input.getOriginalTableName(),
                null,
                newTableName));

        return finalizeResult(context, "Filter completed");
    }

}
