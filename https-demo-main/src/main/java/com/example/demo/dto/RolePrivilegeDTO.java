package com.example.demo.dto;

import com.example.demo.model.Privilege;

import java.util.List;

public class RolePrivilegeDTO {
    private Long roleId;
    private String roleName;
    private List<Privilege> privileges;

    public RolePrivilegeDTO() {
    }

    public RolePrivilegeDTO(Long roleId, String roleName, List<Privilege> privileges) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.privileges = privileges;
    }

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

}
