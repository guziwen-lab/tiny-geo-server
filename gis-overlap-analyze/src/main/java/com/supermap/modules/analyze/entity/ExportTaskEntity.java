package com.supermap.modules.analyze.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.Instant;

import com.supermap.enums.DatasetType;
import com.supermap.enums.UploadStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 导出geo数据
 *
 * @author gzw
 */
@Schema(title = "空间数据导出任务表")
@Data
@TableName("gis_export_task")
public class ExportTaskEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(title = "id")
    private Long id;

    @Schema(title = "需要导出的空间表名")
    private String tableName;

    @Schema(title = "导出类型：SHP, GDB")
    private DatasetType exportType;

    @Schema(title = "文件id")
    private Long fileId;

    @Schema(title = "任务状态：PROCESSING, SUCCESS, FAILED")
    private UploadStatus status;

    @Schema(title = "失败时的错误堆栈/信息")
    private String message;

    @Schema(title = "创建时间")
    private Instant createdAt;

    @Schema(title = "完成时间")
    private Instant finishedAt;

}
