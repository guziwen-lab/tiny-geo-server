package com.supermap.modules.sys.dto;

import java.sql.Timestamp;

import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 部门表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "部门表")
@Data
public class DepartmentDTO extends PageParam {

    @Schema(title = "开始时间")
    private Timestamp startTime;

    @Schema(title = "结束时间")
    private Timestamp endTime;

    @Schema(title = "关键词")
    private String keyword;

    @Schema(title = "上级部门ID")
    private Long parentId;

    @Schema(title = "是否启用")
    private Boolean isActive;

}
