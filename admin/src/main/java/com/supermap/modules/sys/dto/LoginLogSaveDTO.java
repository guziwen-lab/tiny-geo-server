package com.supermap.modules.sys.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 登录日志
 *
 * @author gzw
 */
@Schema(title = "登录日志")
@Data
public class LoginLogSaveDTO {

	@NotNull(groups = Update.class)
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
