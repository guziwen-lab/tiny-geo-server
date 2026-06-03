package com.supermap.modules.sys.vo;

import com.supermap.modules.sys.entity.PermissionEntity;
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
