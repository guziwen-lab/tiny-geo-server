package com.supermap.modules.sys.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 用户表
 *
 * @author gzw
 */
@Schema(title = "用户表")
@Data
public class UserSaveDTO {

	@NotNull(groups = Update.class)
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

    @Schema(title = "角色id")
    private List<Long> roleIds;

    @Schema(title = "部门id")
    private List<Long> deptIds;

}
