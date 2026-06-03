package com.supermap.modules.sys.controller;

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
import com.supermap.modules.sys.dto.LoginLogDTO;
import com.supermap.modules.sys.dto.LoginLogSaveDTO;
import com.supermap.modules.sys.entity.LoginLogEntity;
import com.supermap.modules.sys.service.LoginLogService;

/**
 * 登录日志
 *
 * @author gzw
 */
@Tag(name = "登录日志")
@RestController
@RequestMapping("/sys/loginlog")
@AllArgsConstructor
public class LoginLogController {

    private final LoginLogService loginLogService;

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<LoginLogEntity>> page(@RequestBody LoginLogDTO dto) {
        Page<LoginLogEntity> page = loginLogService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<LoginLogEntity> info(@PathVariable("id") Long id) {
        LoginLogEntity loginLog = loginLogService.getById(id);
        return R.ok(loginLog);
    }

    @Operation(summary = "保存")
    @PostMapping("/save")
    public R<Long> save(@RequestBody @Validated(Add.class) LoginLogSaveDTO dto) {
        Long id = loginLogService.saveDTO(dto);
        return R.ok(id);
    }

    @Operation(summary = "修改")
    @PutMapping("/update")
    public R<Void> update(@RequestBody @Validated(Update.class) LoginLogSaveDTO dto) {
        loginLogService.updateDTOById(dto);
        return R.ok();
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        loginLogService.removeByIds(Arrays.asList(ids));
        return R.ok();
    }

}
