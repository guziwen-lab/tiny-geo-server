package com.supermap.modules.sys.vo;

import com.supermap.modules.sys.entity.LoginLogEntity;
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
public class LoginLogVO extends LoginLogEntity {

}
