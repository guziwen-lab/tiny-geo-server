package com.supermap.admin.modules.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermap.admin.modules.sys.dto.PermissionDTO;
import com.supermap.admin.modules.sys.dto.PermissionSaveDTO;
import com.supermap.admin.modules.sys.entity.PermissionEntity;
import com.supermap.admin.modules.sys.vo.PermissionVO;

import java.util.List;
import java.util.Set;

/**
 * 权限表
 *
 * @author gzw
 */
public interface PermissionService extends IService<PermissionEntity> {

    Page<PermissionEntity> queryPage(PermissionDTO dto);

    List<PermissionEntity> getByRoleId(Long roleId);

    Set<PermissionEntity> getByUserId(Long userId);

    Long saveDTO(PermissionSaveDTO dto);

    void updateDTOById(PermissionSaveDTO dto);

    List<PermissionVO> all();

    void delete(List<Long> permissionIds);

    void batchUpdateSortAndLevel(List<PermissionSaveDTO> dto);

}

