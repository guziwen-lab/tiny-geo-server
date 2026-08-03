package com.supermap.modules.compose.dto;

import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Instant;

/**
 * 组合任务表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "组合任务表")
@Data
public class ComposeDTO extends PageParam {

    @Schema(title = "开始时间")
    private Instant startTime;

    @Schema(title = "结束时间")
    private Instant endTime;

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
