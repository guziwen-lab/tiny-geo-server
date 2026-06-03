package com.supermap.modules.log.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.pojo.R;
import com.supermap.modules.log.dto.AccessDTO;
import com.supermap.modules.log.entity.AccessEntity;
import com.supermap.modules.log.service.AccessService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 全局日志表
 *
 * @author gzw
 */
@Tag(name = "全局日志表")
@RestController
@RequestMapping("/log/access")
@AllArgsConstructor
public class AccessController {

    private final AccessService accessService;

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<AccessEntity>> page(@RequestBody AccessDTO dto) {
        Page<AccessEntity> page = accessService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<AccessEntity> info(@PathVariable("id") Long id) {
        AccessEntity access = accessService.getById(id);
        return R.ok(access);
    }

}
