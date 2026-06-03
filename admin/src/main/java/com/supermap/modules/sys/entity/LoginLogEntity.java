package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

import com.supermap.type.InetTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录日志
 *
 * @author gzw
 */
@Schema(title = "登录日志")
@Data
@TableName("sys_login_log")
public class LoginLogEntity {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	@Schema(title = "主键")
	private Long id;

	@Schema(title = "登录token")
	private String token;

	@Schema(title = "用户id")
	private Long userId;

	@Schema(title = "登录ip")
    @TableField(typeHandler = InetTypeHandler.class)
	private String ip;

	@Schema(title = "是否强制下线")
	private Boolean isForceLogout;

	@Schema(title = "登录时间")
	private Timestamp loginTime;

}
