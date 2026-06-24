package com.supermap.modules.analyze.controller;

import com.supermap.common.pojo.R;
import com.supermap.modules.analyze.service.ExportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author gzw
 */
@Tag(name = "下载geo数据")
@RestController
@RequestMapping("analyze/export")
@AllArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @PostMapping("/shp")
    public R<String> importShp(String path) {
        String url = exportService.exportShp(path);
        return R.ok(url);
    }

    @PostMapping("/gdb")
    public R<String> importGdb(String path, String layerName) {
        String url = exportService.exportGdb(path, layerName);
        return R.ok(url);
    }

}
