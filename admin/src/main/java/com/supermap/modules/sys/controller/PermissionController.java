package com.supermap.modules.sys.controller;

import java.util.Arrays;
import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.valid.group.UpdateSort;
import com.supermap.modules.sys.vo.PermissionVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import com.supermap.common.pojo.R;
import com.supermap.modules.sys.dto.PermissionDTO;
import com.supermap.modules.sys.dto.PermissionSaveDTO;
import com.supermap.modules.sys.entity.PermissionEntity;
import com.supermap.modules.sys.service.PermissionService;

/**
 * 权限/菜单表
 *
 * @author gzw
 */
@Tag(name = "权限/菜单表")
@RestController
@RequestMapping("/sys/permission")
@AllArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @Operation(summary = "全部权限")
    @GetMapping("/all")
    @RequiresPermissions("sys:permission:select")
    public R<List<PermissionVO>> all() {
        List<PermissionVO> all = permissionService.all();
        return R.ok(all);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<PermissionEntity>> page(@RequestBody PermissionDTO dto) {
        Page<PermissionEntity> page = permissionService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{permissionId}")
    public R<PermissionEntity> info(@PathVariable("permissionId") Long permissionId) {
        PermissionEntity permission = permissionService.getById(permissionId);
        return R.ok(permission);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) PermissionSaveDTO dto) {
        Long permissionId = permissionService.saveDTO(dto);
        return R.ok(permissionId);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) PermissionSaveDTO dto) {
        permissionService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "批量修改排序和层级")
    @PutMapping("/batch/update/sort-level")
    public R<Void> batchUpdateSortAndLevel(@RequestBody @Validated(UpdateSort.class) List<PermissionSaveDTO> dto) {
        permissionService.batchUpdateSortAndLevel(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] permissionIds) {
        permissionService.delete(Arrays.asList(permissionIds));
        return R.ok();
    }

}
