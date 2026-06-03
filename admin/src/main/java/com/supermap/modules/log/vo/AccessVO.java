package com.supermap.modules.log.vo;

import com.supermap.modules.log.entity.AccessEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 全局日志表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "全局日志表")
@Data
public class AccessVO extends AccessEntity {

}
