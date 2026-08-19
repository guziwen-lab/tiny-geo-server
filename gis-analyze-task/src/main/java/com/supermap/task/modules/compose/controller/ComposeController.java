package com.supermap.task.modules.compose.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.supermap.core.pojo.R;
import com.supermap.task.modules.compose.dto.ComposeDTO;
import com.supermap.task.modules.compose.entity.ComposeEntity;
import com.supermap.task.modules.compose.service.ComposeService;

/**
 * 组合任务表
 *
 * @author gzw
 */
@Tag(name = "组合任务表")
@RestController
@RequestMapping("/compose/compose")
@AllArgsConstructor
public class ComposeController {

    private final ComposeService composeService;

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<ComposeEntity>> page(@RequestBody ComposeDTO dto) {
        Page<ComposeEntity> page = composeService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<ComposeEntity> info(@PathVariable("id") Long id) {
        ComposeEntity compose = composeService.getById(id);
        return R.ok(compose);
    }

}
