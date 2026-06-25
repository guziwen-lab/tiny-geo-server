package com.supermap.modules.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermap.modules.sys.dto.FileDTO;
import com.supermap.modules.sys.dto.FileDownloadDTO;
import com.supermap.modules.sys.entity.FileEntity;
import com.supermap.modules.sys.vo.FileUrlVO;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;

/**
 * 文件管理表
 *
 * @author gzw
 */
public interface FileService extends IService<FileEntity> {

    String getFilePath(String suffix);

    Page<FileEntity> queryPage(FileDTO dto);

    FileEntity upload(MultipartFile file);

    /**
     * 不自动关流
     */
    FileEntity upload(InputStream inputStream, String fileName);

    Long upload(String content, String fileName);

    Long upload(byte[] bytes, String fileName);

    Long uploadBase64(String base64, String fileNameWithoutSuffix);

    void download(Long id, HttpServletResponse response);

    FileDownloadDTO download(Long id);

    String downloadToBase64(Long id);

    void delete(List<Long> list);

    void increaseRefCount(Long fileId);

    void decreaseRefCount(Long fileId);

    void deleteByRefcount();

    FileUrlVO getUrl(Long fileId);

    String getUrl(String filePath);

}

