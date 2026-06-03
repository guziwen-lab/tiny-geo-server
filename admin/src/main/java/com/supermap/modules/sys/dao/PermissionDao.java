package com.supermap.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.supermap.modules.sys.entity.PermissionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * 权限表
 * 
 * @author gzw
 */
@Mapper
public interface PermissionDao extends BaseMapper<PermissionEntity> {

    List<PermissionEntity> getByRoleId(@Param("roleId") Long roleId);

    Set<PermissionEntity> getByUserId(@Param("userId") Long userId);

    int updateBatchById(@Param("permissionEntities") List<PermissionEntity> permissionEntities);

    List<PermissionEntity> getLoginUserRoute(@Param("userId") Long userId);

}
