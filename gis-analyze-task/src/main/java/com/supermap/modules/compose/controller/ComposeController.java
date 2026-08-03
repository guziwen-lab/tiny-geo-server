package com.supermap.modules.compose.controller;

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
import com.supermap.modules.compose.dto.ComposeDTO;
import com.supermap.modules.compose.dto.ComposeSaveDTO;
import com.supermap.modules.compose.entity.ComposeEntity;
import com.supermap.modules.compose.service.ComposeService;

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

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) ComposeSaveDTO dto) {
        Long id = composeService.saveDTO(dto);
        return R.ok(id);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) ComposeSaveDTO dto) {
        composeService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        composeService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
