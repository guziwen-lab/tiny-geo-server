package com.supermap.modules.sys.dto;

import com.supermap.common.valid.group.Add;
import com.supermap.common.valid.group.Update;
import com.supermap.common.valid.group.UpdateSort;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 权限/菜单表
 *
 * @author gzw
 */
@Schema(title = "权限/菜单表")
@Data
public class PermissionSaveDTO {

    @NotNull(groups = {Update.class, UpdateSort.class})
	@Schema(title = "权限/菜单id")
	private Long permissionId;

	@Schema(title = "父权限/菜单id")
	private Long parentId;

    @NotBlank(groups = Add.class)
	@Schema(title = "权限/菜单名称")
	private String name;

    @Schema(title = "权限key")
	private String permsKey;

	@Schema(title = "菜单路径 (树id的路径，主要用于存放从根节点到当前树的父节点的路径)")
	private String path;

    @NotNull(groups = {Add.class, UpdateSort.class})
	@Schema(title = "菜单层级")
	private Integer level;

    @NotNull(groups = {UpdateSort.class})
	@Schema(title = "排序")
	private Integer sort;

	@Schema(title = "菜单url")
	private String url;

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

    @NotNull(groups = {Update.class, UpdateSort.class})
    @Schema(title = "版本号")
    private Integer version;

}
