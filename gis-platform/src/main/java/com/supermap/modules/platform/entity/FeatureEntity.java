package com.supermap.modules.platform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.sql.Timestamp;

import com.supermap.type.JsonbTypeHandler;
import com.supermap.type.MultiPolygonTypeHandler;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.locationtech.jts.geom.MultiPolygon;

/**
 * geo feature
 *
 * @author gzw
 */
@Schema(title = "geo feature")
@Data
@TableName(value = "geo_feature", autoResultMap = true)
public class FeatureEntity {

	@TableId(value = "id", type = IdType.ASSIGN_ID)
	@Schema(title = "id")
	private Long id;

	@Schema(title = "name")
	private String name;

	@Schema(title = "geom")
	@TableField(typeHandler = MultiPolygonTypeHandler.class)
	private MultiPolygon geom;

	@Schema(title = "properties")
	@TableField(typeHandler = JsonbTypeHandler.class)
	private Object properties;

	@Schema(title = "created at")
	private Timestamp createdAt;

}
