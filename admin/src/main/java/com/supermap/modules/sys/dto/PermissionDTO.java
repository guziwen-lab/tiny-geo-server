package com.supermap.modules.sys.dto;

import java.sql.Timestamp;
import com.supermap.pojo.PageParam;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 权限/菜单表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "权限/菜单表")
@Data
public class PermissionDTO extends PageParam {

	@Schema(title = "开始时间")
	private Timestamp startTime;

	@Schema(title = "结束时间")
	private Timestamp endTime;

	@Schema(title = "权限/菜单id")
	private Long permissionId;

	@Schema(title = "父权限/菜单id")
	private Long parentId;

	@Schema(title = "权限/菜单名称")
	private String name;

	@Schema(title = "权限key")
	private String permsKey;

	@Schema(title = "菜单路径 (树id的路径，主要用于存放从根节点到当前树的父节点的路径)")
	private String path;

	@Schema(title = "菜单层级")
	private Integer level;

	@Schema(title = "排序")
	private Integer sort;

	@Schema(title = "菜单url")
	private String url;

	@Schema(title = "菜单类型 (0: 目录; 1: 页面; 2: 按钮)")
	private Integer type;

	@Schema(title = "菜单图标")
	private String icon;

	@Schema(title = "创建时间")
	private Timestamp createTime;

	@Schema(title = "更新时间")
	private Timestamp updateTime;

}
