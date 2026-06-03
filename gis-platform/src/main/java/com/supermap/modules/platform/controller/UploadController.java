package com.supermap.modules.platform.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supermap.common.pojo.R;
import com.supermap.modules.platform.dto.UploadGeoJsonDTO;
import com.supermap.modules.platform.dto.UploadWktDTO;
import com.supermap.modules.platform.service.UploadService;
import com.supermap.util.GeometryParserUtils;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * @author gzw
 */
@Tag(name = "上传geo数据")
@RestController
@RequestMapping("/platform/upload")
@AllArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    private final ObjectMapper objectMapper;

    private static final int MAX_GEOJSON_SIZE = 5 * 1024 * 1024;

    private static final int MAX_FEATURE_SIZE = 10000;

    private static final Set<String> ALLOWED_GEOMETRY = Set.of(
            "Point", "LineString", "Polygon",
            "MultiPoint", "MultiLineString", "MultiPolygon"
    );

    @PostMapping("/geojson")
    public R<Void> uploadGeoJson(@RequestBody @Validated UploadGeoJsonDTO dto) {
        validateGeoJson(dto.getGeoJson());

        uploadService.uploadGeoJson(dto);
        return R.ok();
    }

    @PostMapping("/wkt")
    public R<Void> uploadWkt(@RequestBody @Validated UploadWktDTO dto) {
        validateWkt(dto.getWkt());

        uploadService.uploadWkt(dto);
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
