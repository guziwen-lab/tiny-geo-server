package com.supermap.modules.sys.controller;

import java.util.Arrays;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import com.supermap.common.pojo.R;
import com.supermap.modules.sys.dto.WorkdayDTO;
import com.supermap.modules.sys.dto.WorkdaySaveDTO;
import com.supermap.modules.sys.entity.WorkdayEntity;
import com.supermap.modules.sys.service.WorkdayService;

/**
 * 工作日表
 *
 * @author gzw
 */
@Tag(name = "工作日表")
@RestController
@RequestMapping("/sys/workday")
public class WorkdayController {

    private final WorkdayService workdayService;

    public WorkdayController(WorkdayService workdayService) {
        this.workdayService = workdayService;
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<WorkdayEntity>> page(@RequestBody WorkdayDTO dto) {
        Page<WorkdayEntity> page = workdayService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<WorkdayEntity> info(@PathVariable("id") Long id) {
        WorkdayEntity workday = workdayService.getById(id);
        return R.ok(workday);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) WorkdaySaveDTO dto) {
        Long id = workdayService.saveDTO(dto);
        return R.ok(id);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) WorkdaySaveDTO dto) {
        workdayService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        workdayService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
