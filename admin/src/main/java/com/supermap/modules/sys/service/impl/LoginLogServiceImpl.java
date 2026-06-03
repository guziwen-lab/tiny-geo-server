package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.supermap.constant.AuthenticationConstant;
import com.supermap.common.util.BeanUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.supermap.modules.sys.dao.LoginLogDao;
import com.supermap.modules.sys.entity.LoginLogEntity;
import com.supermap.modules.sys.service.LoginLogService;
import com.supermap.modules.sys.dto.LoginLogDTO;
import com.supermap.modules.sys.dto.LoginLogSaveDTO;

import java.sql.Timestamp;
import java.util.List;

@Service("loginLogService")
public class LoginLogServiceImpl extends ServiceImpl<LoginLogDao, LoginLogEntity> implements LoginLogService {

    @Override
    public Page<LoginLogEntity> queryPage(LoginLogDTO dto) {
        LambdaQueryWrapper<LoginLogEntity> wrapper = new LambdaQueryWrapper<>();
        return page(dto.page(), wrapper);
    }

    @Override
    public Long saveDTO(LoginLogSaveDTO dto) {
        LoginLogEntity loginLogEntity = new LoginLogEntity();
        BeanUtils.copyProperties(dto, loginLogEntity);
        save(loginLogEntity);
        return loginLogEntity.getId();
    }

    @Override
    public void updateDTOById(LoginLogSaveDTO dto) {
        LoginLogEntity loginLogEntity = new LoginLogEntity();
        BeanUtils.copyProperties(dto, loginLogEntity);
        updateById(loginLogEntity);
    }

    @Override
    public void updateLoginTimeByToken(String token) {
        LoginLogEntity loginLogEntity = new LoginLogEntity();
        loginLogEntity.setToken(token);
        loginLogEntity.setLoginTime(new Timestamp(System.currentTimeMillis()));
        update(loginLogEntity, new LambdaQueryWrapper<LoginLogEntity>().eq(LoginLogEntity::getToken, token));
    }

    @Override
    public LoginLogEntity getByToken(String token) {
        return getOne(new LambdaQueryWrapper<LoginLogEntity>().eq(LoginLogEntity::getToken, token));
    }

    @Override
    public List<LoginLogEntity> getOnline() {
        long l = System.currentTimeMillis() - AuthenticationConstant.DEFAULT_EXPIRE_SECONDS * 1000;

        return list(new LambdaQueryWrapper<LoginLogEntity>()
                .eq(LoginLogEntity::getIsForceLogout, false)
                .ge(LoginLogEntity::getLoginTime, new Timestamp(l)));
    }

    @Override
    public List<LoginLogEntity> getOnlineByUserId(Long userId) {
        long l = System.currentTimeMillis() - AuthenticationConstant.DEFAULT_EXPIRE_SECONDS * 1000;

        return list(new LambdaQueryWrapper<LoginLogEntity>()
                .eq(LoginLogEntity::getUserId, userId)
                .eq(LoginLogEntity::getIsForceLogout, false)
                .ge(LoginLogEntity::getLoginTime, new Timestamp(l)));
    }

    @Override
    public List<LoginLogEntity> getOnlineByRoleId(Long roleId) {
        long l = System.currentTimeMillis() - AuthenticationConstant.DEFAULT_EXPIRE_SECONDS * 1000;
        return baseMapper.getOnlineByRoleId(roleId, new Timestamp(l));
    }

    @Async("logExecutor")
    @Override
    public void asyncSave(LoginLogEntity entity) {
        try {
            save(entity);
        } catch (Exception e) {
            log.error("保存访问日志失败", e);
        }
    }

}