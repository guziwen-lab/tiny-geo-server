package com.supermap.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.sys.entity.UserDepartmentRelationEntity;
import com.supermap.modules.sys.dto.UserDepartmentRelationDTO;
import com.supermap.modules.sys.dto.UserDepartmentRelationSaveDTO;

/**
 * 用户部门关系表
 *
 * @author gzw
 */
public interface UserDepartmentRelationService extends IService<UserDepartmentRelationEntity> {

    Page<UserDepartmentRelationEntity> queryPage(UserDepartmentRelationDTO dto);

    Long saveDTO(UserDepartmentRelationSaveDTO dto);

    void updateDTOById(UserDepartmentRelationSaveDTO dto);

    void removeByUserId(Long userId);

}

