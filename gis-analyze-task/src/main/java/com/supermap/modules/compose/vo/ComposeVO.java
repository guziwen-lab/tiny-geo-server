package com.supermap.modules.compose.vo;

import com.supermap.modules.compose.entity.ComposeEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组合任务表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "组合任务表")
@Data
public class ComposeVO extends ComposeEntity {

}
