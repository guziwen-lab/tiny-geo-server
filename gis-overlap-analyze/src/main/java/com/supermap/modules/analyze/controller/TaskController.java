package com.supermap.modules.analyze.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.analyze.dto.StartTaskDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import com.supermap.common.pojo.R;
import com.supermap.modules.analyze.dto.TaskDTO;
import com.supermap.modules.analyze.dto.TaskSaveDTO;
import com.supermap.modules.analyze.entity.TaskEntity;
import com.supermap.modules.analyze.service.TaskService;

/**
 * 任务表
 *
 * @author gzw
 */
@Tag(name = "任务表")
@RestController
@RequestMapping("/analyze/task")
@AllArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "启动任务")
    @PostMapping("/start/{taskId}")
    public R<Void> start(@PathVariable Long taskId, @RequestBody StartTaskDTO dto) {
        taskService.start(taskId, dto);
        return R.ok();
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<TaskEntity>> page(@RequestBody TaskDTO dto) {
        Page<TaskEntity> page = taskService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<TaskEntity> info(@PathVariable("id") Long id) {
        TaskEntity task = taskService.getById(id);
        return R.ok(task);
    }

    @Operation(summary = "创建任务")
    @PostMapping("/create")
    public R<Long> create(@RequestBody @Validated TaskSaveDTO dto) {
        Long id = taskService.create(dto);
        return R.ok(id);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        taskService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
