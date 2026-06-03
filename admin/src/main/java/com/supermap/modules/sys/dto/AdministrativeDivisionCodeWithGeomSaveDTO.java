package com.supermap.modules.sys.dto;

import com.supermap.common.valid.group.Update;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.sql.Timestamp;

/**
 * 行政区划代码表
 *
 * @author gzw
 */
@Schema(title = "行政区划代码表")
@Data
public class AdministrativeDivisionCodeWithGeomSaveDTO {

	@NotNull(groups = Update.class)
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

	@Schema(title = "几何列")
	private String geom;

	@Schema(title = "创建时间")
	private Timestamp createTime;

	@Schema(title = "更新时间")
	private Timestamp updateTime;

}
