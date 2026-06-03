package com.supermap.modules.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.common.pojo.R;
import com.supermap.modules.sys.dto.FileDTO;
import com.supermap.modules.sys.entity.FileEntity;
import com.supermap.modules.sys.service.FileService;
import com.supermap.modules.sys.vo.FileUrlVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;

/**
 * 文件管理表
 *
 * @author gzw
 */
@Tag(name = "文件管理表")
@RestController
@RequestMapping("/sys/file")
@AllArgsConstructor
public class FileController {

    private final FileService fileService;

    @Operation(summary = "上传文件")
    @PostMapping("/upload")
    public R<FileEntity> upload(@RequestParam(value = "file") MultipartFile file) {
        FileEntity fileEntity = fileService.upload(file);
        return R.ok(fileEntity);
    }

    @Operation(summary = "下载文件")
    @PostMapping("/download/{id}")
    public void download(@PathVariable("id") Long id, HttpServletResponse response) {
        fileService.download(id, response);
    }

    @Operation(summary = "获取文件访问地址")
    @GetMapping("/url/{fileId}")
    public R<FileUrlVO> getUrl(@PathVariable("fileId") Long fileId) {
        FileUrlVO vo = fileService.getUrl(fileId);
        return R.ok(vo);
    }

    @Operation(summary = "分页查询")
    @PostMapping("/page")
    public R<Page<FileEntity>> page(@RequestBody FileDTO dto) {
        Page<FileEntity> page = fileService.queryPage(dto);
        return R.ok(page);
    }

    @Operation(summary = "根据主键查询")
    @GetMapping("/info/{id}")
    public R<FileEntity> info(@PathVariable("id") Long id) {
        FileEntity file = fileService.getById(id);
        return R.ok(file);
    }

    @Operation(summary = "删除")
    @PostMapping("/delete")
    public R<Void> delete(@RequestBody Long[] ids) {
        fileService.delete(Arrays.asList(ids));
        return R.ok();
    }

    @Operation(summary = "删除引用计数为0的文件")
    @PostMapping("/delete/refcount")
    public R<Void> deleteByRefcount() {
        fileService.deleteByRefcount();
        return R.ok();
    }

}
