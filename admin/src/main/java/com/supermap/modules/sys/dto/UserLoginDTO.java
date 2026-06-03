package com.supermap.modules.sys.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 用户登录DTO
 *
 * @author gzw
 */
@Schema(title = "用户登录DTO")
@Data
public class UserLoginDTO {

    @Schema(title = "用户名")
    @NotBlank(message="{sysuser.username.require}")
    private String username;

    @Schema(title = "密码")
    @NotBlank(message="{sysuser.password.require}")
    private String password;

    @Schema(title = "验证码")
    @NotBlank(message="{sysuser.captcha.require}")
    private String captcha;

    @Schema(title = "唯一标识")
    @NotBlank(message="{sysuser.uuid.require}")
    private String uuid;

}
