package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.sql.Timestamp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 权限/菜单表
 *
 * @author gzw
 */
@Schema(title = "权限/菜单表")
@Data
@TableName("sys_permission")
public class PermissionEntity {

	@TableId(value = "permission_id", type = IdType.ASSIGN_ID)
	@Schema(title = "权限/菜单id")
	private Long permissionId;

    @TableField(updateStrategy = FieldStrategy.ALWAYS, insertStrategy = FieldStrategy.ALWAYS)
	@Schema(title = "父权限/菜单id")
	private Long parentId;

	@Schema(title = "权限/菜单名称")
	private String name;

	@Schema(title = "权限key")
	private String permsKey;

	@Schema(title = "菜单层级")
	private Integer level;

	@Schema(title = "排序")
	private Integer sort;

	@Schema(title = "菜单url")
	private String url;

    @TableField(updateStrategy = FieldStrategy.ALWAYS, insertStrategy = FieldStrategy.ALWAYS)
	@Schema(title = "菜单类型 (0: 目录; 1: 页面; 2: 按钮)")
	private Integer type;

	@Schema(title = "菜单图标")
	private String icon;

    @Schema(title = "是否隐藏 (0: 否; 1: 是)")
    private Integer hidden;

    @Schema(title = "打开方式 (0: 内部打开; 1: 外部打开)")
    private Integer openStyle;

	@Schema(title = "创建时间")
	private Timestamp createTime;

	@Schema(title = "更新时间")
	private Timestamp updateTime;

    @Schema(title = "版本号")
    @Version
    private Integer version;

}
