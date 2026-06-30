package com.supermap.vo;

import com.supermap.entity.TaskStepEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 任务执行记录表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "任务执行记录表")
@Data
public class TaskStepVO extends TaskStepEntity {

}
