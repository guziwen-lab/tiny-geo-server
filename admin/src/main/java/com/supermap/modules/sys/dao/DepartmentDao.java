package com.supermap.modules.sys.dao;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.sys.dto.DepartmentDTO;
import com.supermap.modules.sys.entity.DepartmentEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.modules.sys.vo.DepartmentVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;

/**
 * 部门表
 * 
 * @author gzw
 */
@Mapper
public interface DepartmentDao extends BaseMapper<DepartmentEntity> {

    Page<DepartmentVO> page(Page<Object> page, @Param("dto") DepartmentDTO dto);

    List<DepartmentVO> subordinate(@Param("dto") DepartmentDTO dto);

    void disableChildrenById(@Param("id") Long id);

    List<DepartmentEntity> getDepartmentByUserId(@Param("userId") Long userId);

    List<DepartmentEntity> getWithSubordinatesByCodes(@Param("codes") Collection<String> codes);

}
