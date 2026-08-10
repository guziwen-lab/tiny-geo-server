package com.supermap.admin.modules.sys.vo;

import com.supermap.admin.modules.sys.entity.PermissionEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gzw
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PermissionVO extends PermissionEntity {

    private List<PermissionVO> children = new ArrayList<>();

}
