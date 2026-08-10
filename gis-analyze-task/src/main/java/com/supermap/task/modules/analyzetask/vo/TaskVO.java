package com.supermap.task.modules.analyzetask.vo;

import com.supermap.task.modules.analyzetask.entity.TaskDatasetEntity;
import com.supermap.task.modules.analyzetask.entity.TaskEntity;
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
