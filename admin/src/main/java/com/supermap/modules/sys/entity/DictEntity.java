package com.supermap.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 字典表
 *
 * @author gzw
 */
@Schema(title = "字典表")
@Data
@TableName("sys_dict")
public class DictEntity {

	@TableId(value = "dict_id", type = IdType.ASSIGN_ID)
	@Schema(title = "主键")
	private Long dictId;

	@Schema(title = "字典名称")
	private String name;

	@Schema(title = "字典描述")
	private String description;

	@Schema(title = "创建时间")
	private Timestamp createTime;

	@Schema(title = "更新时间")
	private Timestamp updateTime;

}
