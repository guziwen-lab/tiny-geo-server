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
import com.supermap.modules.analyze.dto.TaskStepDTO;
import com.supermap.modules.analyze.dto.TaskStepSaveDTO;
import com.supermap.modules.analyze.entity.TaskStepEntity;
import com.supermap.modules.analyze.service.TaskStepService;

/**
 * 任务执行记录表
 *
 * @author gzw
 */
@Tag(name = "任务执行记录表")
@RestController
@RequestMapping("/analyze/taskstep")
@AllArgsConstructor
public class TaskStepController {

    private final TaskStepService taskStepService;

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<TaskStepEntity>> page(@RequestBody TaskStepDTO dto) {
        Page<TaskStepEntity> page = taskStepService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<TaskStepEntity> info(@PathVariable("id") Long id) {
        TaskStepEntity taskStep = taskStepService.getById(id);
        return R.ok(taskStep);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) TaskStepSaveDTO dto) {
        Long id = taskStepService.saveDTO(dto);
        return R.ok(id);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) TaskStepSaveDTO dto) {
        taskStepService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        taskStepService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
