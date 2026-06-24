package com.supermap.modules.analyze.controller;

import com.supermap.common.pojo.R;
import com.supermap.modules.analyze.service.ImportService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author gzw
 */
@Tag(name = "上传geo数据")
@RestController
@RequestMapping("analyze/import")
@AllArgsConstructor
public class ImportController {

    private final ImportService importService;

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

}
