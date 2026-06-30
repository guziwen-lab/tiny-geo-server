package com.supermap.controller;

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
import com.supermap.dto.TaskDatasetDTO;
import com.supermap.dto.TaskDatasetSaveDTO;
import com.supermap.entity.TaskDatasetEntity;
import com.supermap.service.TaskDatasetService;

/**
 * 图层引用表
 *
 * @author gzw
 */
@Tag(name = "图层引用表")
@RestController
@RequestMapping("/analyze/taskdataset")
@AllArgsConstructor
public class TaskDatasetController {

    private final TaskDatasetService taskDatasetService;

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<TaskDatasetEntity>> page(@RequestBody TaskDatasetDTO dto) {
        Page<TaskDatasetEntity> page = taskDatasetService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<TaskDatasetEntity> info(@PathVariable("id") Long id) {
        TaskDatasetEntity taskDataset = taskDatasetService.getById(id);
        return R.ok(taskDataset);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) TaskDatasetSaveDTO dto) {
        Long id = taskDatasetService.saveDTO(dto);
        return R.ok(id);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) TaskDatasetSaveDTO dto) {
        taskDatasetService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        taskDatasetService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
