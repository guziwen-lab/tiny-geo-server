package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

import com.supermap.type.JsonbTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 部门表
 *
 * @author gzw
 */
@Schema(title = "部门表")
@Data
@TableName(value = "sys_department", autoResultMap = true)
public class DepartmentEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(title = "部门ID")
    private Long id;

    @Schema(title = "上级部门ID")
    private Long parentId;

    @Schema(title = "部门名称")
    private String name;

    @Schema(title = "部门编码")
    private String code;

    @Schema(title = "部门层级")
    private Integer depth;

    @Schema(title = "排序")
    private Integer sortOrder;

    @Schema(title = "是否启用")
    private Boolean isActive;

    @Schema(title = "扩展字段")
    @TableField(typeHandler = JsonbTypeHandler.class)
    private Object metadata;

    @Schema(title = "备注")
    private String remark;

    @Schema(title = "创建时间")
    private Timestamp createdAt;

    @Schema(title = "更新时间")
    private Timestamp updatedAt;

}
