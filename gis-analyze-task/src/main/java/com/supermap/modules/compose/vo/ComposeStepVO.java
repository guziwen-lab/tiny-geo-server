package com.supermap.modules.compose.vo;

import com.supermap.modules.compose.entity.ComposeStepEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 组合任务任务关联表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "组合任务任务关联表")
@Data
public class ComposeStepVO extends ComposeStepEntity {

}
