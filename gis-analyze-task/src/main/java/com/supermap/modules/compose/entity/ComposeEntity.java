package com.supermap.modules.compose.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 组合任务表
 *
 * @author gzw
 */
@Schema(title = "组合任务表")
@Data
@TableName("gis_compose")
public class ComposeEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "任务名称")
    private String name;

    @Schema(title = "状态")
    private String status;

    @Schema(title = "结果表schema")
    private String schemaName;

    @Schema(title = "附加信息")
    private String message;

    @Schema(title = "耗时(毫秒)")
    private Long cost;

    @Schema(title = "创建时间")
    private Instant createdAt;

    @Schema(title = "开始时间")
    private Instant startedAt;

    @Schema(title = "结束时间")
    private Instant finishedAt;

}
