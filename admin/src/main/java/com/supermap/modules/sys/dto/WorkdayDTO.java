package com.supermap.modules.sys.dto;

import java.sql.Date;
import java.sql.Timestamp;
import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 工作日表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "工作日表")
@Data
public class WorkdayDTO extends PageParam {

	@Schema(title = "开始时间")
	private Timestamp startTime;

	@Schema(title = "结束时间")
	private Timestamp endTime;

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
