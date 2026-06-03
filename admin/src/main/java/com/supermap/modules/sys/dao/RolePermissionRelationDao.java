package com.supermap.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.modules.sys.entity.RolePermissionRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色权限关系表
 * 
 * @author gzw
 */
@Mapper
public interface RolePermissionRelationDao extends BaseMapper<RolePermissionRelationEntity> {
	
}
