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
import com.supermap.dto.DatasetDTO;
import com.supermap.dto.DatasetSaveDTO;
import com.supermap.entity.DatasetEntity;
import com.supermap.service.DatasetService;

/**
 * 数据集表
 *
 * @author gzw
 */
@Tag(name = "数据集表")
@RestController
@RequestMapping("/analyze/dataset")
@AllArgsConstructor
public class DatasetController {

    private final DatasetService datasetService;

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<DatasetEntity>> page(@RequestBody DatasetDTO dto) {
        Page<DatasetEntity> page = datasetService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<DatasetEntity> info(@PathVariable("id") Long id) {
        DatasetEntity dataset = datasetService.getById(id);
        return R.ok(dataset);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) DatasetSaveDTO dto) {
        Long id = datasetService.saveDTO(dto);
        return R.ok(id);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) DatasetSaveDTO dto) {
        datasetService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        datasetService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
