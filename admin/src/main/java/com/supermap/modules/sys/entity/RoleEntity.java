package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * 角色表
 *
 * @author gzw
 */
@Schema(title = "角色表")
@Data
@TableName("sys_role")
public class RoleEntity {

    /**
     * 角色id
     */
    @TableId(value = "role_id", type = IdType.ASSIGN_ID)
    @Schema(title = "角色id")
    private Long roleId;
    /**
     * 角色名称
     */
    @Schema(title = "角色名称")
    private String roleName;
    /**
     * 备注
     */
    @Schema(title = "备注")
    private String remark;
    /**
     * 创建时间
     */
    @Schema(title = "创建时间")
    private Date createTime;
    /**
     * 更新时间
     */
    @Schema(title = "更新时间")
    private Date updateTime;

}
