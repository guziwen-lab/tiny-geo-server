package com.supermap.task.modules.task.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StartTaskDTO extends TaskSaveDTO {

    private String resultTableName;

}
