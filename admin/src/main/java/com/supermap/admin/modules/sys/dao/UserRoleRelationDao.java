package com.supermap.admin.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.admin.modules.sys.entity.UserRoleRelationEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户角色关系表
 * 
 * @author gzw
 */
@Mapper
public interface UserRoleRelationDao extends BaseMapper<UserRoleRelationEntity> {
	
}
