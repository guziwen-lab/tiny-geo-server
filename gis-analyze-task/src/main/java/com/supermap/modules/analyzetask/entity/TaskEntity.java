package com.supermap.modules.analyzetask.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.Instant;

import com.supermap.enums.AnalysisType;
import com.supermap.enums.GeomType;
import com.supermap.enums.TaskStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 任务表
 *
 * @author gzw
 */
@Schema(title = "任务表")
@Data
@TableName("gis_task")
public class TaskEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "任务名称")
    private String taskName;

    @Schema(title = "分析类型")
    private AnalysisType analysisType;

    @Schema(title = "构建任务参数")
    private String taskParam;

    @Schema(title = "状态")
    private TaskStatus status;

    @Schema(title = "结果数据集id")
    private Long resultDatasetId;

    @Schema(title = "几何类型")
    private GeomType geomType;

    @Schema(title = "附加信息")
    private String message;

    @Schema(title = "耗时")
    private Long cost;

    @Schema(title = "创建时间")
    private Instant createdAt;

    @Schema(title = "开始时间")
    private Instant startedAt;

    @Schema(title = "完成时间")
    private Instant finishedAt;

}
