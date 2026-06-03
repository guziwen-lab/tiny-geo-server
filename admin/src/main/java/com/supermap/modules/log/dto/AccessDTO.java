package com.supermap.modules.log.dto;

import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.sql.Timestamp;

/**
 * 全局日志表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "全局日志表")
@Data
public class AccessDTO extends PageParam {

	@Schema(title = "开始时间")
	private Timestamp startTime;

	@Schema(title = "结束时间")
	private Timestamp endTime;

	@Schema(title = "主键")
	private Long id;

	@Schema(title = "链路追踪id")
	private String traceId;

	@Schema(title = "标签")
	private String label;

	@Schema(title = "类名")
	private String className;

	@Schema(title = "方法名")
	private String methodName;

	@Schema(title = "参数")
	private String params;

	@Schema(title = "结果")
	private String result;

	@Schema(title = "耗时")
	private Long cost;

	@Schema(title = "错误信息")
	private String errorMsg;

	@Schema(title = "异常")
	private String exception;

	@Schema(title = "请求用户")
	private Long requestUser;

	@Schema(title = "创建时间")
	private Timestamp createdAt;

}
