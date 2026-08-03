package com.supermap.task.impl;

import com.supermap.AnalysisContext;
import com.supermap.AnalysisResult;
import com.supermap.AnalysisStep;
import com.supermap.LayerInfo;
import com.supermap.common.util.StringUtils;
import com.supermap.enums.AnalysisType;
import com.supermap.enums.GeomType;
import com.supermap.security.SqlInjectionCheck;
import com.supermap.service.GeometryService;
import com.supermap.task.AbstractAnalysisTask;
import com.supermap.task.param.RepairGeometryParam;
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

    private final GeometryService geometryService;

    @Override
    protected AnalysisResult doExecute(AnalysisContext<RepairGeometryParam> context) {
        LayerInfo layer = context.getInputLayers().get(0);
        String schema = context.getSchema();
        String tableName = layer.getTableName();
        String newTableName = context.getResultTableName();

        SqlInjectionCheck.checkTableName(tableName, newTableName);

        // 用新表名复制表
        RepairGeometryParam param = context.getParam();
        GeomType geomType = context.getGeomType();
        geometryService.copyTable(tableName,
                newTableName,
                schema,
                layer.getColumns(),
                layer.getSrid(),
                geomType,
                param.getSrid());
        geometryService.createGistIndex(schema, newTableName);

        context.addStep(new AnalysisStep(1,
                layer.getOriginalTableName(),
                null,
                context.getResultTableName()));

        return finalizeResult(context, newTableName, "Filter completed");
    }

    @Override
    protected void validate(AnalysisContext<RepairGeometryParam> context) {
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
