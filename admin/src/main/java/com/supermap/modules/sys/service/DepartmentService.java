package com.supermap.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.sys.entity.DepartmentEntity;
import com.supermap.modules.sys.dto.DepartmentDTO;
import com.supermap.modules.sys.dto.DepartmentSaveDTO;
import com.supermap.modules.sys.vo.DepartmentVO;

import java.util.Collection;
import java.util.List;

/**
 * 部门表
 *
 * @author gzw
 */
public interface DepartmentService extends IService<DepartmentEntity> {

    Page<DepartmentVO> queryPage(DepartmentDTO dto);

    List<DepartmentVO> subordinate(DepartmentDTO dto);

    Long saveDTO(DepartmentSaveDTO dto);

    void updateDTOById(DepartmentSaveDTO dto);

    List<DepartmentVO> tree(Boolean isActive);

    List<DepartmentEntity> getByUserId(Long userId);

    List<DepartmentEntity> getWithSubordinatesByCodes(Collection<String> codes);

}

