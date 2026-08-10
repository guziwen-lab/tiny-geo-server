package com.supermap.admin.modules.sys.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.supermap.db.dto.SearchDTO;
import com.supermap.admin.modules.sys.dto.UserSaveDTO;
import com.supermap.admin.modules.sys.entity.UserEntity;
import com.supermap.admin.modules.sys.vo.UserVO;
import com.supermap.admin.shiro.LoginUser;

import java.util.List;

/**
 * 用户表
 *
 * @author gzw
 */
public interface UserService extends IService<UserEntity> {

    Page<UserVO> queryPage(SearchDTO dto);

    UserEntity getByUsername(String username);

    Long saveDTO(UserSaveDTO dto);

    void updateDTO(UserSaveDTO dto);

    void reset(String username);

    void resetAll();

    void setLoginUserPermsInfo(LoginUser principal);

    UserVO getUserVO(Long userId);

    void delete(List<Long> userIds);

    void unlock(Long userId);

}

