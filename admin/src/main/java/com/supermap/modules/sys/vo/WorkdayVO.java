package com.supermap.modules.sys.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Date;

/**
 * 工作日表
 *
 * @author gzw
 */
@Schema(title = "工作日表")
@Data
public class WorkdayVO {

	@Schema(title = "主键")
	private Long id;

	@Schema(title = "日期")
	private Date date;

	@Schema(title = "年份")
	private Integer year;

	@Schema(title = "月份")
	private Integer month;

	@Schema(title = "日")
	private Integer day;

	@Schema(title = "状态 (0: 普通工作日; 1: 周末双休日; 2: 需要补班的工作日; 3: 法定节假日)")
	private Integer status;

}
