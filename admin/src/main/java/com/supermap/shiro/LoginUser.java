package com.supermap.shiro;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.supermap.modules.sys.entity.DepartmentEntity;
import com.supermap.modules.sys.entity.PermissionEntity;
import com.supermap.modules.sys.entity.RoleEntity;
import com.supermap.modules.sys.entity.UserEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author gzw
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class LoginUser extends UserEntity {

    private String token;

    @JsonIgnore
    private String password;

    private Set<String> stringPermissions = new HashSet<>();

    private Set<String> permissionNames = new HashSet<>();

    private Collection<PermissionEntity> permissionEntities = new HashSet<>();

    private Set<String> roles = new HashSet<>();

    private Collection<RoleEntity> roleEntities = new HashSet<>();

    private Set<String> buttons = new HashSet<>();

    private Set<String> departments = new HashSet<>();

    private Collection<DepartmentEntity> departmentEntities = new HashSet<>();

}
