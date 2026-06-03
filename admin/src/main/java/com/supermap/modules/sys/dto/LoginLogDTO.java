package com.supermap.modules.sys.dto;

import java.sql.Timestamp;
import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录日志
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "登录日志")
@Data
public class LoginLogDTO extends PageParam {

	@Schema(title = "开始时间")
	private Timestamp startTime;

	@Schema(title = "结束时间")
	private Timestamp endTime;

	@Schema(title = "主键")
	private Long id;

	@Schema(title = "登录token")
	private String token;

	@Schema(title = "用户id")
	private Long userId;

	@Schema(title = "登录ip")
	private String ip;

	@Schema(title = "是否强制下线")
	private Boolean isForceLogout;

	@Schema(title = "登录时间")
	private Timestamp loginTime;

}
