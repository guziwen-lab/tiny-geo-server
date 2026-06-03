package com.supermap.modules.sys.dao;

import com.supermap.modules.sys.entity.UserDepartmentRelationEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户部门关系表
 * 
 * @author gzw
 */
@Mapper
public interface UserDepartmentRelationDao extends BaseMapper<UserDepartmentRelationEntity> {
	
}
