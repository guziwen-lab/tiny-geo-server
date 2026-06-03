package com.supermap.modules.sys.vo;

import com.supermap.modules.sys.entity.DepartmentEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 部门表
 *
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Schema(title = "部门表")
@Data
public class DepartmentVO extends DepartmentEntity {

    @Schema(title = "是否有子部门")
    private Boolean hasChildren;

    @Schema(title = "子部门")
    private List<DepartmentVO> children;

}
