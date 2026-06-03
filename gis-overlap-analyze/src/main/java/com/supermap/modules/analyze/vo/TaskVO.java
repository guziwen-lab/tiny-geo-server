package com.supermap.modules.analyze.vo;

import com.supermap.modules.analyze.entity.TaskEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "任务表")
@Data
public class TaskVO extends TaskEntity {

}
