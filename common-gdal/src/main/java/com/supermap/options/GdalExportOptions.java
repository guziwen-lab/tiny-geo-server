package com.supermap.options;

import com.supermap.CommandExecutor;
import com.supermap.GdalProperties;
import com.supermap.enums.DatasetType;
import com.supermap.enums.GeomType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gzw
 */
@Component
@RequiredArgsConstructor
public class GdalExportOptions {

    private final CommandExecutor executor;

    private final GdalProperties gdalProperties;

    public void execExport(String tableName,
                           String targetPath,
                           DatasetType exportType,
                           GeomType geomType,
                           boolean append) {
        List<String> command = buildCommand(tableName, targetPath, exportType, geomType, append);
        executor.execute(command);
    }

    private List<String> buildCommand(String tableName,
                                      String targetPath,
                                      DatasetType exportType,
                                      GeomType geomType,
                                      boolean append) {
        String schema = gdalProperties.getSchema();
        String qualifiedTableName = schema + "." + tableName;

        List<String> command = new ArrayList<>();
        command.add("ogr2ogr");
        command.add("-f");

        // 根据传入类型适配 GDAL 驱动名
        if (DatasetType.SHP == exportType) {
            command.add("ESRI Shapefile");
            // 附带编码配置防中文乱码
            command.add("--config");
            command.add("SHAPE_ENCODING");
            command.add("TUF-8");
        } else if (DatasetType.GDB == exportType) {
            command.add("OpenFileGDB");
        } else {
            throw new IllegalArgumentException("不支持的导出类型: " + exportType);
        }

        // 追加模式：多表导出到同一个目标文件时，第二张表起需要 -update -append
        if (append) {
            command.add("-update");
            command.add("-append");
        }

        // 明确指定输出几何类型，避免泛型 geometry 列导致导出失败
        command.add("-nlt");
        command.add(geomType.getOgr2ogrNltValue());

        command.add(targetPath); // 目标输出文件/文件夹路径
        command.add(gdalProperties.getPgConnect());     // 源数据库连接串
        command.add(qualifiedTableName);  // schema.tableName

        return command;
    }

}
