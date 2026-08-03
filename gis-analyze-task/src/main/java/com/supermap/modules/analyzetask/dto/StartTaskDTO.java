package com.supermap.modules.analyzetask.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class StartTaskDTO extends TaskSaveDTO {

    private String resultLayerName;

    private String resultTableName;

}
