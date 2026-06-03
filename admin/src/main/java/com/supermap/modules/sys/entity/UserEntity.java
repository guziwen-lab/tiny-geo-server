package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 用户表
 *
 * @author gzw
 */
@Schema(title = "用户表")
@Data
@TableName("sys_user")
public class UserEntity {

	@TableId(value = "user_id", type = IdType.ASSIGN_ID)
	@Schema(title = "用户id")
	private Long userId;

	@Schema(title = "用户名")
	private String username;

	@Schema(title = "密码")
	private String password;

    @Schema(title = "昵称")
    private String nickname;

    @Schema(title = "头像")
    private Long avatar;

	@Schema(title = "创建时间")
	private Timestamp createTime;

	@Schema(title = "更新时间")
	private Timestamp updateTime;

}
