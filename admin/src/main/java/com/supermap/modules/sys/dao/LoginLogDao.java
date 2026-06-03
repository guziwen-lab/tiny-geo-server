package com.supermap.modules.sys.dao;

import com.supermap.modules.sys.entity.LoginLogEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.Timestamp;
import java.util.List;

/**
 * 登录日志
 * 
 * @author gzw
 */
@Mapper
public interface LoginLogDao extends BaseMapper<LoginLogEntity> {

    List<LoginLogEntity> getOnlineByRoleId(@Param("roleId") Long roleId, @Param("loginTime") Timestamp loginTime);

}
