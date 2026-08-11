package com.supermap.analyze.service.impl;

import com.supermap.analyze.AnalysisContext;
import com.supermap.analyze.LayerInfo;
import com.supermap.analyze.task.param.IntersectSplitParam;
import com.supermap.gis.enums.GeomType;
import com.supermap.gis.type.Column;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class IntersectSplitExecuteServiceTest {

    @Test
    void buildsSqlWithUniqueConflictingFieldsAndAreaSplits() {
        IntersectSplitParam param = new IntersectSplitParam(
                List.of(new IntersectSplitParam.SplitField("jcmj", "jcmj_split")),
                List.of(new IntersectSplitParam.SplitField("jcmj", "dltb_jcmj_split")),
                "zt_ratio",
                "dltb_ratio");
        param.setIntersectAreaThreshold("10");
        param.setIntersectRatioThreshold("0.2");

        AnalysisContext<IntersectSplitParam> context = new AnalysisContext<>();
        context.setSchema("gis");
        context.setParam(param);
        context.setGeomType(GeomType.MULTI_POLYGON);
        context.setSrid(4490);

        String sql = new TestableIntersectSplitExecuteService().buildSql(
                layer("zt", new Column("jcmj", "numeric"), new Column("code", "varchar")),
                layer("dltb", new Column("jcmj", "numeric"), new Column("code", "varchar")),
                "result_layer",
                context);

        assertTrue(sql.contains("CREATE TABLE gis.result_layer AS"));
        assertTrue(sql.contains("a.\"jcmj\" AS \"jcmj\""));
        assertTrue(sql.contains("b.\"jcmj\" AS \"jcmj_1\""));
        assertTrue(sql.contains("COALESCE(t2.\"jcmj\", 0)"));
        assertTrue(sql.contains("COALESCE(t2.\"jcmj_1\", 0)"));
        assertTrue(sql.contains("ST_Relate(a.geom, b.geom, '2********')"));
        assertTrue(sql.contains("inter_area >= 10"));
        assertTrue(sql.contains(">= 0.2"));
    }

    private static LayerInfo layer(String tableName, Column... columns) {
        LayerInfo layer = new LayerInfo();
        layer.setTableName(tableName);
        layer.setColumns(List.of(columns));
        return layer;
    }

    private static class TestableIntersectSplitExecuteService extends IntersectSplitExecuteService {
        String buildSql(LayerInfo current,
                        LayerInfo next,
                        String resultTableName,
                        AnalysisContext<IntersectSplitParam> context) {
            return buildExecuteSql(current, next, resultTableName, context);
        }
    }
}
