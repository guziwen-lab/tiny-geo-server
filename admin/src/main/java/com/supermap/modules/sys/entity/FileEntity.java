package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 文件管理表
 *
 * @author gzw
 */
@Schema(title = "文件管理表")
@Data
@TableName("sys_file")
public class FileEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "文件名称")
    private String fileName;

    @Schema(title = "文件路径")
    private String filePath;

    @Schema(title = "文件MIME类型")
    private String fileType;

    @Schema(title = "文件大小 (字节)")
    private Long fileSize;

    @Schema(title = "存储方式 (local/oss/cos...)")
    private String storageType;

    @Schema(title = "创建时间")
    private Timestamp createTime;

    @Schema(title = "更新时间")
    private Timestamp updateTime;

    @Schema(title = "引用计数")
    private Integer refCount;

}
