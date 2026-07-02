package com.supermap.modules.analyzetask.vo;

import com.supermap.modules.analyzetask.entity.TaskDatasetEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 图层引用表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "图层引用表")
@Data
public class TaskDatasetVO extends TaskDatasetEntity {

}
