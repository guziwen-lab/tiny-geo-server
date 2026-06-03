package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 行政区划代码表
 *
 * @author gzw
 */
@Schema(title = "行政区划代码表")
@Data
@TableName("sys_administrative_division_code")
public class AdministrativeDivisionCodeEntity {

    @TableId(value = "id", type = IdType.ASSIGN_ID)
    @Schema(title = "主键")
    private Long id;

    @Schema(title = "行政区划代码")
    private String code;

    @Schema(title = "上级行政区划代码")
    private String pcode;

    @Schema(title = "行政区划名称")
    private String name;

    @Schema(title = "行政区划级别 (0: 国家; 1: 省; 2: 市; 3: 区县)")
    private Integer level;

    @Schema(title = "行政区划路径")
    private Integer path;

    @Schema(title = "创建时间")
    private Date createTime;

    @Schema(title = "更新时间")
    private Date updateTime;

    @Schema(title = "子节点")
    @TableField(exist = false)
    List<AdministrativeDivisionCodeEntity> children = new ArrayList<>();

}
