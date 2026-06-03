package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典项表
 *
 * @author gzw
 */
@Schema(title = "字典项表")
@Data
@TableName("sys_dict_item")
public class DictItemEntity {

	@TableId(value = "dict_item_id", type = IdType.ASSIGN_ID)
	@Schema(title = "主键")
	private Long dictItemId;

	@Schema(title = "字典id")
	private Long dictId;

	@Schema(title = "上级字典项id")
    @TableField(updateStrategy = FieldStrategy.ALWAYS, insertStrategy = FieldStrategy.ALWAYS)
	private Long parentId;

	@Schema(title = "字典项名称")
	private String name;

    @Schema(title = "字典项编码")
    private String code;

	@Schema(title = "排序")
	private Integer sort;

	@Schema(title = "创建时间")
	private Timestamp createTime;

	@Schema(title = "更新时间")
	private Timestamp updateTime;

	@TableField(exist = false)
	@Schema(title = "子节点")
	private List<DictItemEntity> children = new ArrayList<>();

}
