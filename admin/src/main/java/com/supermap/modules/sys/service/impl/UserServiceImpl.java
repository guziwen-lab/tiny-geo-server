package com.supermap.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.supermap.constant.AuthenticationConstant;
import com.supermap.constant.MenuType;
import com.supermap.common.util.BeanUtils;
import com.supermap.common.util.CollectionUtils;
import com.supermap.common.util.StringUtils;
import com.supermap.modules.sys.dao.UserDao;
import com.supermap.dto.SearchDTO;
import com.supermap.modules.sys.dto.UserSaveDTO;
import com.supermap.modules.sys.entity.*;
import com.supermap.modules.sys.service.*;
import com.supermap.modules.sys.vo.UserVO;
import com.supermap.shiro.LoginUser;
import com.supermap.shiro.encoder.PasswordEncoder;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

@Service("userService")
@AllArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserDao, UserEntity> implements UserService {

    private final PasswordEncoder passwordEncoder;

    private final PermissionService permissionService;

    private final RoleServiceImpl roleService;

    private final DepartmentService departmentService;

    private final UserRoleRelationService userRoleRelationService;

    private final UserDepartmentRelationService userDepartmentRelationService;

    private final FileService fileService;

    private final RedisTemplate<String, String> redisTemplate;

    private final LoginUserService loginUserService;

    @Override
    public Page<UserVO> queryPage(SearchDTO dto) {
        return baseMapper.page(dto.page(), dto);
    }

    @Override
    public UserEntity getByUsername(String username) {
        return getOne(new LambdaQueryWrapper<>(UserEntity.class).eq(UserEntity::getUsername, username));
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public Long saveDTO(UserSaveDTO dto) {
        UserEntity username = getByUsername(dto.getUsername());
        if (username != null) {
            throw new IllegalArgumentException("用户名已存在");
        }

        UserEntity userEntity = new UserEntity();
        Timestamp now = new Timestamp(System.currentTimeMillis());
        userEntity.setCreateTime(now);
        userEntity.setUpdateTime(now);
        BeanUtils.copyProperties(dto, userEntity);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));

        try {
            save(userEntity);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("用户名已存在");
        }

        saveRoleByUserId(userEntity.getUserId(), dto.getRoleIds());
        saveDepartmentByUserId(userEntity.getUserId(), dto.getDeptIds());

        if (userEntity.getAvatar() != null)
            fileService.increaseRefCount(userEntity.getAvatar());

        return userEntity.getUserId();
    }

    private void saveDepartmentByUserId(Long userId, List<Long> departmentIds) {
        userDepartmentRelationService.removeByUserId(userId);

        if (CollectionUtils.isEmpty(departmentIds))
            return;

        long count = departmentService.count(new LambdaQueryWrapper<DepartmentEntity>()
                .in(DepartmentEntity::getId, departmentIds));
        if (count != departmentIds.size())
            throw new IllegalArgumentException("部门不存在");

        List<UserDepartmentRelationEntity> relationEntities = departmentIds.stream()
                .map(item -> {
                    UserDepartmentRelationEntity userDepartmentRelationEntity = new UserDepartmentRelationEntity();
                    userDepartmentRelationEntity.setUserId(userId);
                    userDepartmentRelationEntity.setDepartmentId(item);
                    return userDepartmentRelationEntity;
                }).toList();
        userDepartmentRelationService.saveBatch(relationEntities);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void updateDTO(UserSaveDTO dto) {
        UserEntity user = getById(dto.getUserId());
        if (user == null)
            throw new IllegalArgumentException("用户不存在");

        UserEntity update = new UserEntity();
        BeanUtils.copyProperties(dto, update);
        if (StringUtils.isNotBlank(dto.getPassword())) {
            update.setPassword(passwordEncoder.encode(dto.getPassword()));
        } else {
            update.setPassword(null);
        }
        update.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        try {
            updateById(update);
        } catch (DataIntegrityViolationException ex) {
            throw new DataIntegrityViolationException("用户名已存在");
        }

        saveRoleByUserId(dto.getUserId(), dto.getRoleIds());
        saveDepartmentByUserId(dto.getUserId(), dto.getDeptIds());

        if (update.getAvatar() != null && !Objects.equals(user.getAvatar(), update.getAvatar())) {
            fileService.increaseRefCount(update.getAvatar());
            fileService.decreaseRefCount(user.getAvatar());
        }

        // 修改用户信息后要清空用户登录失败次数
        clearLoginRetryInfo(user.getUsername());

        // 修改了用户信息要更新这个用户登录的信息
        loginUserService.refreshLoginUserInfoByUserId(dto.getUserId());
    }

    private void clearLoginRetryInfo(String username) {
        redisTemplate.delete(AuthenticationConstant.LOGIN_RETRY_KEY_PREFIX + username);
    }

    private void saveRoleByUserId(Long userId, List<Long> roleIds) {
        userRoleRelationService.removeByUserId(userId);

        if (CollectionUtils.isEmpty(roleIds))
            return;

        long count = roleService.count(new LambdaQueryWrapper<RoleEntity>()
                .in(RoleEntity::getRoleId, roleIds));
        if (count != roleIds.size())
            throw new IllegalArgumentException("角色不存在");

        List<UserRoleRelationEntity> relationEntities = roleIds.stream()
                .map(item -> {
                    UserRoleRelationEntity userRoleRelationEntity = new UserRoleRelationEntity();
                    userRoleRelationEntity.setUserId(userId);
                    userRoleRelationEntity.setRoleId(item);
                    return userRoleRelationEntity;
                }).toList();
        userRoleRelationService.saveBatch(relationEntities);
    }

    @Override
    public void reset(String username) {
        UserEntity user = getByUsername(username);
        if (user == null)
            throw new IllegalArgumentException("用户不存在");

        UserEntity update = new UserEntity();
        update.setUserId(user.getUserId());
        update.setPassword("123456");
        update.setUpdateTime(new Timestamp(System.currentTimeMillis()));
        updateById(update);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void resetAll() {
        List<UserEntity> updateList = new ArrayList<>();

        List<UserEntity> list = list();
        for (UserEntity userEntity : list) {
            UserEntity update = new UserEntity();
            update.setUserId(userEntity.getUserId());
            update.setPassword("123456");
            update.setUpdateTime(new Timestamp(System.currentTimeMillis()));
            updateList.add(update);
        }

        updateBatchById(updateList);
    }

    @Override
    public void setLoginUserPermsInfo(LoginUser loginUser) {
        List<RoleEntity> roleEntities = roleService.getByUserId(loginUser.getUserId());
        Set<String> roleNames = roleEntities.stream().map(RoleEntity::getRoleName).collect(Collectors.toSet());
        loginUser.setRoleEntities(roleEntities);
        loginUser.setRoles(roleNames);

        Set<String> permissionNames = new HashSet<>();
        Set<String> stringPermissions = new HashSet<>();

        Set<PermissionEntity> permissionEntities = permissionService.getByUserId(loginUser.getUserId());
        for (PermissionEntity pe : permissionEntities) {
            Integer type = pe.getType();

            permissionNames.add(pe.getName());

            if (StringUtils.isNotBlank(pe.getPermsKey())) {
                String permsKey = pe.getPermsKey().trim();

                stringPermissions.add(permsKey);
            }

            if (type != null && type == MenuType.BUTTON) {
                loginUser.getButtons().add(pe.getName());
            }
        }

        loginUser.setPermissionEntities(permissionEntities);
        loginUser.setPermissionNames(permissionNames);
        loginUser.setStringPermissions(stringPermissions);

        List<DepartmentEntity> departmentEntities = departmentService.getByUserId(loginUser.getUserId());
        loginUser.setDepartmentEntities(departmentEntities);
        loginUser.setDepartments(departmentEntities.stream()
                .map(DepartmentEntity::getCode)
                .collect(Collectors.toSet()));
    }

    @Override
    public UserVO getUserVO(Long userId) {
        UserVO userVO = new UserVO();
        UserEntity userEntity = getById(userId);
        BeanUtils.copyProperties(userEntity, userVO);

        List<RoleEntity> roles = roleService.getByUserId(userId);
        userVO.setRoleEntities(roles);

        List<DepartmentEntity> departmentEntities = departmentService.getByUserId(userId);
        userVO.setDepartmentEntities(departmentEntities);

        return userVO;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public void delete(List<Long> userIds) {
        List<Long> fileIds = list(new LambdaQueryWrapper<UserEntity>()
                .in(UserEntity::getUserId, userIds))
                .stream().map(UserEntity::getAvatar)
                .filter(Objects::nonNull).toList();
        fileService.delete(fileIds);

        removeByIds(userIds);
        userRoleRelationService.removeByUserIds(userIds);
    }

    @Override
    public void unlock(Long userId) {
        UserEntity userEntity = getById(userId);
        if (userEntity == null)
            throw new IllegalArgumentException("用户不存在");
        clearLoginRetryInfo(userEntity.getUsername());
    }

}