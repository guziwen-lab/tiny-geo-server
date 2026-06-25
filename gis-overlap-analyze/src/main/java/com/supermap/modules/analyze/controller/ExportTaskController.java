package com.supermap.modules.analyze.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import com.supermap.common.pojo.R;
import com.supermap.modules.analyze.dto.ExportTaskDTO;
import com.supermap.modules.analyze.dto.ExportTaskSaveDTO;
import com.supermap.modules.analyze.entity.ExportTaskEntity;
import com.supermap.modules.analyze.service.ExportTaskService;

/**
 * 导出geo数据
 *
 * @author gzw
 */
@Tag(name = "导出geo数据")
@RestController
@RequestMapping("/analyze/exporttask")
@AllArgsConstructor
public class ExportTaskController {

    private final ExportTaskService exportTaskService;

    @PostMapping("/shp")
    public R<Long> exportShp(@RequestParam String tableName) {
        Long taskId = exportTaskService.exportShp(tableName);
        return R.ok(taskId);
    }

    @PostMapping("/gdb")
    public R<Long> exportGdb(@RequestParam String tableName) {
        Long taskId = exportTaskService.exportGdb(tableName);
        return R.ok(taskId);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<ExportTaskEntity>> page(@RequestBody ExportTaskDTO dto) {
        Page<ExportTaskEntity> page = exportTaskService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<ExportTaskEntity> info(@PathVariable("id") Long id) {
        ExportTaskEntity exportTask = exportTaskService.getById(id);
        return R.ok(exportTask);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) ExportTaskSaveDTO dto) {
        Long id = exportTaskService.saveDTO(dto);
        return R.ok(id);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) ExportTaskSaveDTO dto) {
        exportTaskService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        exportTaskService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
