package com.supermap.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.dto.SearchDTO;
import com.supermap.modules.sys.entity.RoleEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 角色表
 * 
 * @author gzw
 */
@Mapper
public interface RoleDao extends BaseMapper<RoleEntity> {

    Set<String> getRoleNamesByUserId(@Param("userId") Long userId);

    List<RoleEntity> getRoleByUserId(@Param("userId") Long userId);

    Page<RoleEntity> page(Page<Object> page, @Param("dto") SearchDTO dto);

}
