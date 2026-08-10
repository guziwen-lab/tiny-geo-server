package com.supermap.gdal;

import com.supermap.command.CommandExecutor;
import com.supermap.command.CommandResult;
import com.supermap.gdal.config.GdalProperties;
import com.supermap.gis.enums.DatasetType;
import com.supermap.gis.enums.GeomType;
import com.supermap.gdal.info.LayerMeta;
import com.supermap.gdal.info.parser.GdalInfoParser;
import com.supermap.gdal.info.parser.GdalLayerInfoParser;
import com.supermap.gdal.options.GdalExportOptions;
import com.supermap.gdal.options.GdalImportOptions;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gzw
 */
@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(GdalProperties.class)
public class GdalTool {

    private final CommandExecutor executor;

    private final GdalProperties gdalProperties;

    private final GdalImportOptions gdalImportOptions;

    private final GdalExportOptions gdalExportOptions;

    public List<String> listGdbLayers(String gdbPath) {
        List<String> command = new ArrayList<>();
        command.add(gdalProperties.getOgrInfo());
        command.add("-so");
        command.add(gdbPath);

        CommandResult execute = executor.execute(command);
        String stdout = execute.stdout();
        return GdalLayerInfoParser.parse(stdout);
    }

    public LayerMeta queryLayerMeta(String path, String layerName) {
        List<String> command = new ArrayList<>();
        command.add(gdalProperties.getOgrInfo());
        command.add("-so");
        command.add(path);
        command.add(layerName);

        CommandResult execute = executor.execute(command);
        String stdout = execute.stdout();
        return GdalInfoParser.parse(stdout);
    }

    public void importLayer(String sourcePath,
                            String tableName,
                            String layerName,
                            boolean isAppend,
                            GeomType targetGeomType,
                            Integer srid,
                            String encoding) {
        gdalImportOptions.execImport(sourcePath, tableName, layerName, isAppend, targetGeomType, srid, encoding);
    }

    public void exportGdb(String tableName,
                          String targetPath,
                          GeomType geomType,
                          boolean append) {
        gdalExportOptions.execExport(tableName, targetPath, DatasetType.GDB, geomType, append);
    }

    public void exportShp(String tableName,
                          String targetPath,
                          GeomType geomType,
                          boolean append) {
        gdalExportOptions.execExport(tableName, targetPath, DatasetType.SHP, geomType, append);
    }

}
