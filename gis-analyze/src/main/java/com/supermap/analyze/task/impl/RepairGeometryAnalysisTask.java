package com.supermap.analyze.task.impl;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.AnalysisResult;
import com.supermap.analyze.AnalysisStep;
import com.supermap.analyze.LayerInfo;
import com.supermap.analyze.service.impl.RepairGeometryExecuteService;
import com.supermap.core.common.util.StringUtils;
import com.supermap.analyze.enums.AnalysisType;
import com.supermap.gis.enums.GeomType;
import com.supermap.analyze.security.SqlInjectionCheck;
import com.supermap.analyze.task.AbstractAnalysisTask;
import com.supermap.analyze.task.param.RepairGeometryParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author gzw
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RepairGeometryAnalysisTask extends AbstractAnalysisTask<RepairGeometryParam> {

    private final RepairGeometryExecuteService repairGeometryExecuteService;

    @Override
    protected AnalysisResult doExecute(AnalysisContext<RepairGeometryParam> context) {
        LayerInfo layer = context.getInputLayers().get(0);
        String newTableName = context.getResultTableName();
        SqlInjectionCheck.checkTableName(newTableName);

        repairGeometryExecuteService.execute(layer, null, newTableName, context);

        context.addStep(new AnalysisStep(1,
                layer.getOriginalTableName(),
                null,
                newTableName));

        return finalizeResult(context, "Filter completed");
    }

    @Override
    protected void validate(AnalysisContext<RepairGeometryParam> context) {
        if (context.getParam() == null)
            throw new IllegalArgumentException("分析参数不能为空");

        List<LayerInfo> layers = context.getInputLayers();
        if (layers == null || layers.size() != 1) {
            throw new IllegalArgumentException("修复几何类型需要且仅需要1个图层");
        }
    }

    @Override
    public AnalysisType getType() {
        return AnalysisType.REPAIR_GEOMETRY;
    }

    /**
     * 属性过滤：保持输入几何类型
     *
     * @param context 分析上下文
     * @return 输入几何类型
     */
    @Override
    public GeomType resultGeomType(AnalysisContext<RepairGeometryParam> context) {
        List<LayerInfo> layerInfos = context.getInputLayers();
        return layerInfos.get(0).getGeomType();
    }

    @Override
    public RepairGeometryParam buildParam(String srid) {
        if (StringUtils.isEmpty(srid))
            return new RepairGeometryParam();

        try {
            Integer sridInt = Integer.valueOf(srid);
            return new RepairGeometryParam(sridInt);
        } catch (NumberFormatException e) {
            throw new RuntimeException("参数转换srid失败", e);
        }
    }

}
