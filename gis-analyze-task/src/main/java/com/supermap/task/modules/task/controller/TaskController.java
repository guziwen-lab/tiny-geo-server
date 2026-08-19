package com.supermap.task.modules.task.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.task.modules.task.dto.StartTaskDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.supermap.core.pojo.R;
import com.supermap.task.modules.task.dto.TaskDTO;
import com.supermap.task.modules.task.dto.TaskSaveDTO;
import com.supermap.task.modules.task.entity.TaskEntity;
import com.supermap.task.modules.task.service.TaskService;

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

    @Operation(summary = "创建任务")
    @PostMapping("/create")
    public R<Long> create(@RequestBody @Validated TaskSaveDTO dto) {
        Long id = taskService.create(dto).getId();
        return R.ok(id);
    }

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
    public R<TaskEntity> info(@PathVariable Long id) {
        TaskEntity task = taskService.getById(id);
        return R.ok(task);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        taskService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
