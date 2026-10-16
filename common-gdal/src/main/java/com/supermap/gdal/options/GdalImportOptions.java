package com.supermap.gdal.options;

import com.supermap.command.CommandExecutor;
import com.supermap.gdal.config.GdalProperties;
import com.supermap.core.util.StringUtils;
import com.supermap.gis.enums.GeomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gzw
 */
@Component
@RequiredArgsConstructor
public class GdalImportOptions {

    private final CommandExecutor executor;

    private final GdalProperties gdalProperties;

    public void execImport(String sourcePath,
                           String tableName,
                           String layerName,
                           boolean isAppend,
                           GeomType targetGeomType,
                           Integer srid,
                           String encoding) {
        List<String> command = buildCommand(sourcePath, tableName, layerName, isAppend, targetGeomType, srid, encoding);
        executor.execute(command);
    }

    private List<String> buildCommand(String sourcePath,
                                      String tableName,
                                      String layerName,
                                      boolean isAppend,
                                      GeomType targetGeomType,
                                      Integer srid,
                                      String encoding) {
        List<String> command = new ArrayList<>();
        command.add(gdalProperties.getOgr2Ogr());
        command.add("-f");
        command.add("PostgreSQL");
        if (isAppend) {
            command.add("-append");
            command.add("-addfields");
        } else {
            command.add("-overwrite");
        }
        command.add(gdalProperties.getPgConnect());
        command.add(sourcePath);

        if (srid != null) {
            command.add("-t_srs");
            command.add("EPSG:" + srid);
        }

        command.add("-nln");

        if (isAppend) {
            command.add(gdalProperties.getSchema() + "." + tableName);
        } else {
            command.add(tableName);

            // -lco 为图层创建选项，仅在新建表时生效，追加模式下无需传递
            command.add("-lco");
            command.add("GEOMETRY_NAME=geom");
            // 统一源要素主键列名，供叠加结果追溯及面积守恒校验使用。
            command.add("-lco");
            command.add("FID=" + gdalProperties.getPkColumnName());
            command.add("-lco");
            command.add("SPATIAL_INDEX=NONE");
            command.add("-lco");
            command.add("SCHEMA=" + gdalProperties.getSchema());
        }

        if (layerName != null) {
            command.add(layerName);
        }

        if (StringUtils.isNotBlank(encoding)) {
            command.add("--config");
            command.add("SHAPE_ENCODING");
            command.add(encoding);
        }

        if (targetGeomType != null && targetGeomType.isMulti()) {
            // 以目标 Multi 类型显式转换，forceTo 同时覆盖两类不匹配场景：
            // shp 头声明 Polygon 实际含 MultiPolygon 的提升，以及
            // GDB MultiSurface 曲面向 MultiPolygon 的线性化降维（PROMOTE_TO_MULTI 无法处理后者）
            command.add("-nlt");
            command.add(targetGeomType.getOgr2ogrNltValue());
        } else if (!isAppend && sourcePath.toLowerCase().endsWith(".shp")) {
            // 未取得目标类型时兜底：Shapefile 头声明的 Polygon 图层可能实际包含 MultiPolygon，
            // 强制提升为 Multi 类型避免 PostgreSQL COPY 阶段几何类型不匹配
            command.add("-nlt");
            command.add("PROMOTE_TO_MULTI");
        }
        // 追加且目标为非 Multi 类型时：预校验已保证源数据不含 Multi 类型，无需转换

        return command;
    }

}
