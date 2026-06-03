package com.supermap.modules.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.dto.SearchDTO;
import com.supermap.modules.sys.entity.UserEntity;
import com.supermap.modules.sys.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 用户表
 *
 * @author gzw
 */
@Mapper
public interface UserDao extends BaseMapper<UserEntity> {

    Page<UserVO> page(Page<Object> page, @Param("dto") SearchDTO dto);

    @Select("select * from sys_user where username = #{username}")
    UserEntity getByUsername(String username);

}
