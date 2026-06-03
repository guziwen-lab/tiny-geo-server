package com.supermap.modules.sys.dto;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 角色表
 *
 * @author gzw
 */
@Schema(title = "角色表")
@Data
public class RoleSaveDTO {

    @Schema(title = "角色id")
    @NotNull(groups = Update.class)
    private Long roleId;

    @Schema(title = "角色名称")
    @NotBlank(groups = Add.class)
    private String roleName;

    @Schema(title = "备注")
    private String remark;

    @Schema(title = "权限id")
    private List<Long> permissionIds;

}
