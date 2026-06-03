package com.supermap.dto;

import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 分页查询搜索DTO
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "分页查询搜索DTO")
@Data
public class SearchDTO extends PageParam {

	@Schema(title = "开始时间")
	private Timestamp startTime;

	@Schema(title = "结束时间")
	private Timestamp endTime;

    @Schema(title = "关键词")
    private String keyword;

}
