package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 用户角色关系表
 *
 * @author gzw
 */
@Schema(title = "用户角色关系表")
@Data
@TableName("sys_user_role_relation")
public class UserRoleRelationEntity {

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(title = "主键")
    private Long id;
    /**
     * 用户id
     */
    @Schema(title = "用户id")
    private Long userId;
    /**
     * 角色id
     */
    @Schema(title = "角色id")
    private Long roleId;

}
