package com.supermap.modules.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.pojo.R;
import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import com.supermap.modules.sys.dto.RoleSaveDTO;
import com.supermap.dto.SearchDTO;
import com.supermap.modules.sys.entity.RoleEntity;
import com.supermap.modules.sys.service.RoleService;
import com.supermap.modules.sys.vo.RoleVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 角色表
 *
 * @author gzw
 */
@Tag(name = "角色表")
@RestController
@RequestMapping("/sys/role")
@AllArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @Operation(summary = "全部角色")
    @GetMapping("/all")
    @RequiresPermissions("sys:role:select")
    public R<List<RoleEntity>> all() {
        List<RoleEntity> all = roleService.all();
        return R.ok(all);
    }

    @Operation(summary = "列表")
    @PostMapping("/list")
    public R<Page<RoleEntity>> list(@RequestBody SearchDTO dto) {
        Page<RoleEntity> page = roleService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "信息")
    @GetMapping("/info/{roleId}")
    public R<RoleVO> info(@PathVariable("roleId") Long roleId) {
        RoleVO role = roleService.getVOById(roleId);
        return R.ok(role);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) RoleSaveDTO dto) {
        Long roleId = roleService.saveDTO(dto);
        return R.ok(roleId);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) RoleSaveDTO dto) {
        roleService.updateDTO(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] roleIds) {
        roleService.delete(Arrays.asList(roleIds));
        return R.ok();
    }

}
