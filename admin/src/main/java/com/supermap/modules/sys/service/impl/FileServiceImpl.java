package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermap.common.util.*;
import com.supermap.modules.sys.dao.FileDao;
import com.supermap.modules.sys.dto.FileDTO;
import com.supermap.modules.sys.dto.FileDownloadDTO;
import com.supermap.modules.sys.entity.FileEntity;
import com.supermap.modules.sys.service.FileService;
import com.supermap.modules.sys.vo.FileUrlVO;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;

@Service("fileService")
public class FileServiceImpl extends ServiceImpl<FileDao, FileEntity> implements FileService {

    @Value("${file.base-path}")
    private String basePath;

    @Value("${file.external-url-prefix}")
    private String externalUrlPrefix;

    private final Tika tika;

    public FileServiceImpl(Tika tika) {
        this.tika = tika;
    }

    @Override
    public Page<FileEntity> queryPage(FileDTO dto) {
        LambdaQueryWrapper<FileEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    private String getFilePath() {
        LocalDate today = LocalDate.now();
        String year = String.valueOf(today.getYear());
        String month = String.format("%02d", today.getMonthValue());  // 格式化为两位数字
        String day = String.format("%02d", today.getDayOfMonth());    // 格式化为两位数字

        return basePath + File.separator + year + File.separator + month + File.separator + day + File.separator;
    }

    @Override
    public FileEntity upload(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream()) {
            return upload(inputStream, file.getOriginalFilename());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileEntity upload(InputStream inputStream, String fileName) {
        FileEntity fileEntity = new FileEntity();
        String dest = getFilePath() + UUIDUtils.get() + "." + FileNameUtils.getSuffix(fileName);
        File destFile = new File(dest);
        try (FileOutputStream outputStream = new FileOutputStream(dest)) {
            long size = IOUtils.copy(inputStream, outputStream);

            fileEntity.setFileName(fileName);
            fileEntity.setFilePath(dest);
            String mimeType = detectMimeType(destFile);
            fileEntity.setFileType(mimeType);
            fileEntity.setFileSize(size);
            fileEntity.setStorageType("local");
            Timestamp now = new Timestamp(System.currentTimeMillis());
            fileEntity.setCreateTime(now);
            fileEntity.setUpdateTime(now);
            fileEntity.setRefCount(0);
            save(fileEntity);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + fileName, e);
        }

        return fileEntity;
    }

    @Override
    public Long upload(String content, String fileName) {
        return upload(content.getBytes(StandardCharsets.UTF_8), fileName);
    }

    @Override
    public Long upload(byte[] bytes, String fileName) {
        FileEntity fileEntity = new FileEntity();
        String dest = getFilePath() + UUIDUtils.get() + "." + FileNameUtils.getSuffix(fileName);
        File destFile = FileUtils.touch(dest);
        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destFile))) {
            outputStream.write(bytes);

            fileEntity.setFileName(fileName);
            fileEntity.setFilePath(dest);
            String mimeType = detectMimeType(destFile);
            fileEntity.setFileType(mimeType);
            fileEntity.setFileSize(destFile.length());
            fileEntity.setStorageType("local");
            Timestamp now = new Timestamp(System.currentTimeMillis());
            fileEntity.setCreateTime(now);
            fileEntity.setUpdateTime(now);
            save(fileEntity);
        } catch (IOException e) {
            FileUtils.del(destFile);
            throw new RuntimeException("文件上传失败: " + fileName, e);
        }
        return fileEntity.getId();
    }

    @Override
    public Long uploadBase64(String base64, String fileNameWithoutSuffix) {
        byte[] decode = Base64.getDecoder().decode(base64);
        Base64FileTypeDetectorUtils.FileType detect = Base64FileTypeDetectorUtils.detect(base64);
        if (detect == Base64FileTypeDetectorUtils.FileType.UNKNOWN)
            throw new RuntimeException("未知文件类型");

        String fileName = fileNameWithoutSuffix + "." + detect.getExtension();
        return upload(decode, fileName);
    }

    private String detectMimeType(File file) {
        String mimeType = URLConnection.guessContentTypeFromName(file.getName());
        if (mimeType == null) {
            try {
                mimeType = tika.detect(file);
            } catch (IOException ignored) {
            }
        }
        return mimeType != null ? mimeType : "application/octet-stream";
    }

    @Override
    public void download(Long id, HttpServletResponse response) {
        FileEntity fileEntity = getById(id);
        if (fileEntity == null)
            throw new RuntimeException("文件不存在");

        response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
        String fileName = fileEntity.getFileName();
        String disposition = "attachment; filename=" + URLEncoder.encode(fileName, StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", disposition);

        try (OutputStream outputStream = response.getOutputStream();
             FileInputStream inputStream = new FileInputStream(fileEntity.getFilePath())) {
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public FileDownloadDTO download(Long id) {
        FileEntity fileEntity = getById(id);
        if (fileEntity == null)
            throw new RuntimeException("文件不存在");

        try (FileInputStream inputStream = new FileInputStream(fileEntity.getFilePath());
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            IOUtils.copy(inputStream, outputStream);

            FileDownloadDTO fileDownloadDTO = new FileDownloadDTO();
            BeanUtils.copyProperties(fileEntity, fileDownloadDTO);
            fileDownloadDTO.setFile(outputStream.toByteArray());
            return fileDownloadDTO;
        } catch (IOException e) {
            throw new RuntimeException("文件下载失败: " + fileEntity.getFileName(), e);
        }
    }

    @Override
    public String downloadToBase64(Long id) {
        FileDownloadDTO download = download(id);
        return Base64.getEncoder().encodeToString(download.getFile());
    }

    @Override
    public void delete(List<Long> ids) {
        if (CollectionUtils.isEmpty(ids))
            return;

        List<FileEntity> list = list(new LambdaQueryWrapper<FileEntity>()
                .in(FileEntity::getId, ids));
        for (FileEntity fileEntity : list)
            FileUtils.del(fileEntity.getFilePath());

        removeByIds(ids);
    }

    @Override
    public void increaseRefCount(Long fileId) {
        if (fileId == null)
            return;
        FileEntity file = getById(fileId);
        if (file == null)
            throw new RuntimeException("文件不存在");

        FileEntity update = new FileEntity();
        update.setId(fileId);
        update.setRefCount(file.getRefCount() + 1);
        updateById(update);
    }

    @Override
    public void decreaseRefCount(Long fileId) {
        if (fileId == null)
            return;
        FileEntity file = getById(fileId);
        if (file == null)
            throw new RuntimeException("文件不存在");

        FileEntity update = new FileEntity();
        update.setId(fileId);
        update.setRefCount(Math.max((file.getRefCount() - 1), 0));
        updateById(update);
    }

    @Override
    public void deleteByRefcount() {
        List<FileEntity> list = list(new LambdaQueryWrapper<FileEntity>()
                .eq(FileEntity::getRefCount, 0));
        for (FileEntity fileEntity : list)
            FileUtils.del(fileEntity.getFilePath());

        removeByIds(list.stream().map(FileEntity::getId).toList());
    }

    @Override
    public FileUrlVO getUrl(Long fileId) {
        FileEntity fileEntity = getById(fileId);
        if (fileEntity == null)
            throw new RuntimeException("文件不存在");

        String url = getUrl(fileEntity.getFilePath());

        FileUrlVO vo = new FileUrlVO();
        BeanUtils.copyProperties(fileEntity, vo);
        vo.setUrl(url);
        return vo;
    }

    @Override
    public String getUrl(String filePath) {
        /*
         * nginx 配置映射目录
         * location /file {
         *     alias /Users/guziwen/Java/temp/general-backend/file;
         * }
         */
        return filePath.replaceFirst(basePath, externalUrlPrefix);
    }

}