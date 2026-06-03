package com.supermap.modules.sys.controller;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.sys.vo.DepartmentVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import com.supermap.common.pojo.R;
import com.supermap.modules.sys.dto.DepartmentDTO;
import com.supermap.modules.sys.dto.DepartmentSaveDTO;
import com.supermap.modules.sys.entity.DepartmentEntity;
import com.supermap.modules.sys.service.DepartmentService;

/**
 * 部门表
 *
 * @author gzw
 */
@Tag(name = "部门表")
@RestController
@RequestMapping("/sys/department")
@AllArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "树形全部")
    @GetMapping("/tree")
    public R<List<DepartmentVO>> tree(@RequestParam(required = false) Boolean isActive) {
        List<DepartmentVO> page = departmentService.tree(isActive);
        return R.ok(page);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<DepartmentVO>> page(@RequestBody DepartmentDTO dto) {
        Page<DepartmentVO> page = departmentService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "查询下级")
    @PostMapping("/subordinate")
    public R<List<DepartmentVO>> subordinate(@RequestBody DepartmentDTO dto) {
        List<DepartmentVO> list = departmentService.subordinate(dto);
        return R.ok(list);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<DepartmentEntity> info(@PathVariable("id") Long id) {
        DepartmentEntity department = departmentService.getById(id);
        return R.ok(department);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) DepartmentSaveDTO dto) {
        Long id = departmentService.saveDTO(dto);
        return R.ok(id);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) DepartmentSaveDTO dto) {
        departmentService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        departmentService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
