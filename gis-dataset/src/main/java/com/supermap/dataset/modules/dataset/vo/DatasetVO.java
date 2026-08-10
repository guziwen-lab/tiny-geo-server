package com.supermap.dataset.modules.dataset.vo;

import com.supermap.dataset.modules.dataset.entity.DatasetEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 数据集表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "数据集表")
@Data
public class DatasetVO extends DatasetEntity {

}
