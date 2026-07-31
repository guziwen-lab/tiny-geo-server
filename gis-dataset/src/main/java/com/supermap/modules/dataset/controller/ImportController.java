package com.supermap.modules.dataset.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.supermap.common.pojo.R;
import com.supermap.common.util.CollectionUtils;
import com.supermap.common.util.StringUtils;
import com.supermap.modules.dataset.dto.UploadGeoJsonDTO;
import com.supermap.modules.dataset.dto.UploadWktDTO;
import com.supermap.modules.dataset.dto.BatchImportGdbDTO;
import com.supermap.modules.dataset.service.ImportService;
import com.supermap.util.GeometryParserUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * @author gzw
 */
@Tag(name = "上传geo数据")
@RestController
@RequestMapping("analyze/import")
@AllArgsConstructor
public class ImportController {

    private final ImportService importService;

    private static final int MAX_GEOJSON_SIZE = 5 * 1024 * 1024;

    private static final int MAX_FEATURE_SIZE = 10000;

    private static final Set<String> ALLOWED_GEOMETRY = Set.of(
            "Point", "LineString", "Polygon",
            "MultiPoint", "MultiLineString", "MultiPolygon"
    );

    @PostMapping("/shp")
    public R<Long> importShp(String path) {
        Long id = importService.importShp(path);
        return R.ok(id);
    }

    @PostMapping("/gdb")
    public R<List<Long>> importGdb(String path, String layerName) {
        List<Long> ids = importService.importGdb(path, layerName);
        return R.ok(ids);
    }

    /**
     * 按实际 SRID（以及不能混存的图层、几何类型）将一批 GDB 归并为数据集。
     * SRID 是坐标处理的最小颗粒度，不同高斯分带不会被写入同一张表。
     */
    @PostMapping("/gdb/batch/grouping")
    public R<List<Long>> importGdbBatchByGrouping(@RequestBody @Validated List<BatchImportGdbDTO> dtoList) {
        List<Long> ids = importService.importGdbBatchByGrouping(dtoList);
        return R.ok(ids);
    }

    @PostMapping("/shp/batch")
    public R<Long> importShpBatch(String path,
                                  String layerName,
                                  Integer srid,
                                  String encoding,
                                  String tableName,
                                  String shpFilePattern) {
        File dir = new File(path);
        File[] provinces = dir.listFiles();
        if (provinces == null)
            throw new IllegalArgumentException("文件夹为空");

        Pattern pattern = Pattern.compile(shpFilePattern);

        List<String> paths = new ArrayList<>();
        for (File province : provinces) {
            File[] counties = province.listFiles();
            if (counties == null) continue;

            for (File county : counties) {
                File[] files = county.listFiles(f -> f.isFile() && pattern.matcher(f.getName()).matches());
                if (files == null) continue;
                paths.addAll(Arrays.stream(files).map(File::getAbsolutePath).toList());
            }
        }

        if (CollectionUtils.isEmpty(paths))
            throw new IllegalArgumentException("文件夹为空");

        Long id = importService.importShpBatch(paths, layerName, srid, encoding, tableName);
        return R.ok(id);
    }

    @PostMapping("/gdb/batch")
    public R<Long> importGdbBatch(String path, String layerName, Integer srid, String tableName) {
        File dir = new File(path);
        File[] files = dir.listFiles(f -> f.isDirectory() && f.getName().endsWith(".gdb"));

        if (files == null)
            throw new IllegalArgumentException("文件夹为空");

        List<String> paths = new ArrayList<>(files.length);
        for (File file : files) {
            paths.add(file.getAbsolutePath());
        }

        Long id = importService.importGdbBatch(paths, layerName, srid, tableName);
        return R.ok(id);
    }

    @PostMapping("/append/shp")
    public R<Long> importShpAppend(String path, Long datasetId) {
        Long id = importService.importShp(path, datasetId);
        return R.ok(id);
    }

    @PostMapping("/append/gdb")
    public R<Long> importGdbAppend(String path, String layerName, Long datasetId) {
        if (StringUtils.isEmpty(layerName))
            throw new IllegalArgumentException("图层名称不能为空");

        Long id = importService.importGdb(path, layerName, datasetId);
        return R.ok(id);
    }

    @PostMapping("/geojson")
    public R<Void> uploadGeoJson(@RequestBody @Validated UploadGeoJsonDTO dto) {
        validateGeoJson(dto.getGeoJson());

        importService.uploadGeoJson(dto);
        return R.ok();
    }

    @PostMapping("/wkt")
    public R<Void> uploadWkt(@RequestBody @Validated UploadWktDTO dto) {
        validateWkt(dto.getWkt());

        importService.uploadWkt(dto);
        return R.ok();
    }

    private void validateWkt(String wkt) {
        GeometryParserUtils.parseWKT(wkt);
    }

    private void validateGeoJson(JsonNode root) {
        if (root == null || root.isNull()) {
            throw new IllegalArgumentException("GeoJSON不能为空");
        }

        if (root.toString().length() > MAX_GEOJSON_SIZE) {
            throw new IllegalArgumentException("GeoJSON过大");
        }

        if (!root.has("type")) {
            throw new IllegalArgumentException("GeoJSON缺少type字段");
        }

        String type = root.get("type").asText();

        if ("Feature".equals(type)) {
            validateFeature(root);
        } else if ("FeatureCollection".equals(type)) {
            validateFeatureCollection(root);
        } else {
            throw new IllegalArgumentException("只支持Feature或FeatureCollection");
        }
    }

    private void validateFeature(JsonNode node) {
        if (!node.has("geometry") || node.get("geometry").isNull()) {
            throw new IllegalArgumentException("Feature缺少geometry");
        }

        if (!node.has("properties")) {
            throw new IllegalArgumentException("Feature缺少properties");
        }

        JsonNode geometry = node.get("geometry");
        if (!geometry.has("type") ||
                !ALLOWED_GEOMETRY.contains(geometry.get("type").asText())) {
            throw new IllegalArgumentException("不支持的geometry类型");
        }
    }

    private void validateFeatureCollection(JsonNode node) {
        if (!node.has("features") || !node.get("features").isArray()) {
            throw new IllegalArgumentException("FeatureCollection缺少features数组");
        }

        int size = node.get("features").size();

        if (size == 0) {
            throw new IllegalArgumentException("features不能为空");
        }

        if (size > MAX_FEATURE_SIZE) {
            throw new IllegalArgumentException("features数量过多");
        }
    }

}
