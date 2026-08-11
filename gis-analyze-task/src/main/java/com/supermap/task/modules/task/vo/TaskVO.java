package com.supermap.task.modules.task.vo;

import com.supermap.task.modules.task.entity.TaskDatasetEntity;
import com.supermap.task.modules.task.entity.TaskEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 任务表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "任务表")
@Data
public class TaskVO extends TaskEntity {

    private List<TaskDatasetEntity> taskDatasetEntities;

}
