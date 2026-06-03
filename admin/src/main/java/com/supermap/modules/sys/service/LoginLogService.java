package com.supermap.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.modules.sys.entity.LoginLogEntity;
import com.supermap.modules.sys.dto.LoginLogDTO;
import com.supermap.modules.sys.dto.LoginLogSaveDTO;

import java.util.List;

/**
 * 登录日志
 *
 * @author gzw
 */
public interface LoginLogService extends IService<LoginLogEntity> {

    Page<LoginLogEntity> queryPage(LoginLogDTO dto);

    Long saveDTO(LoginLogSaveDTO dto);

    void updateDTOById(LoginLogSaveDTO dto);

    void updateLoginTimeByToken(String token);

    LoginLogEntity getByToken(String token);

    List<LoginLogEntity> getOnline();

    List<LoginLogEntity> getOnlineByUserId(Long userId);

    List<LoginLogEntity> getOnlineByRoleId(Long roleId);

    void asyncSave(LoginLogEntity entity);

}

