package com.supermap.admin.modules.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermap.db.dto.SearchDTO;
import com.supermap.admin.modules.sys.dto.RoleSaveDTO;
import com.supermap.admin.modules.sys.entity.RoleEntity;
import com.supermap.admin.modules.sys.vo.RoleVO;

import java.util.List;
import java.util.Set;

/**
 * 角色表
 *
 * @author gzw
 */
public interface RoleService extends IService<RoleEntity> {

    Page<RoleEntity> queryPage(SearchDTO dto);

    Set<String> getRoleNamesByUserId(Long userId);

    Long saveDTO(RoleSaveDTO dto);

    void updateDTO(RoleSaveDTO dto);

    List<RoleEntity> all();

    List<RoleEntity> getByUserId(Long userId);

    void delete(List<Long> roleIds);

    RoleVO getVOById(Long roleId);

}

