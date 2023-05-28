package com.example.demo.dto;

import com.example.demo.model.Privilege;

import java.util.List;

public class RolePrivilegeDTO {
    private Long roleId;
    private String roleName;

    private String privilegeName;
    private Privilege privilege;

    public RolePrivilegeDTO() {
    }

    public RolePrivilegeDTO(Long roleId, String privilegeName) {
        this.roleId = roleId;
        this.privilegeName = privilegeName;
    }

    public String getPrivilegeName() {
        return privilegeName;
    }

    public void setPrivilegeName(String privilegeName) {
        this.privilegeName = privilegeName;
    }

    public RolePrivilegeDTO(Long roleId, String roleName, Privilege privileges) {
        this.roleId = roleId;
        this.roleName = roleName;
        this.privilege = privileges;
    }

    public Privilege getPrivilege() {
        return privilege;
    }

    public void setPrivilege(Privilege privilege) {
        this.privilege = privilege;
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
