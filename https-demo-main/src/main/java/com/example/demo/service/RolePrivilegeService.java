package com.example.demo.service;

import com.example.demo.dto.RolePrivilegeDTO;
import com.example.demo.dto.RolesDTO;
import com.example.demo.model.Privilege;
import com.example.demo.model.Role;
import com.example.demo.repo.PrivilegeRepo;
import com.example.demo.repo.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Permission;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class RolePrivilegeService {

    @Autowired
    private RoleRepo roleRepository;
    @Autowired
    private PrivilegeRepo privilegeRepository;


    public Role GetRoleById(Long roleId){
        Role role  = roleRepository.findById(roleId).orElseGet(null);
        return role;

    }
    public void DeleteRolePermission(RolePrivilegeDTO rolePrivilegeDTO){
        Role role = GetRoleById(rolePrivilegeDTO.getRoleId());
        List<Privilege> privilegesList = (List)role.getPrivileges();
        Privilege privilege = rolePrivilegeDTO.getPrivilege();

        for(int i = 0; i<privilegesList.size(); i++) {
                if(privilegesList.get(i).getId() == privilege.getId())
                    privilegesList.remove(i);
            }
        roleRepository.save(role);
    }

    public void AddRolePermission(RolePrivilegeDTO rolePrivilegeDTO) {
        Role role = GetRoleById(rolePrivilegeDTO.getRoleId());
        List<Privilege> privilegesList = (List) role.getPrivileges();
        if (privilegesList.isEmpty()) {
            privilegesList.add(rolePrivilegeDTO.getPrivilege());
        } else {
            privilegesList.add(privilegesList.size(), rolePrivilegeDTO.getPrivilege());
//            for (int i = 0; i <= privilegesList.size(); i++) {
//                    if (privilegesList.get(i).getId() == rolePrivilegeDTO.getPrivilege().getId()) {
//                        i++;
//                    } else {
//                        privilegesList.add(i, rolePrivilegeDTO.getPrivilege());
//                    }
//                }
           }
        roleRepository.save(role);
    }

    public List<Privilege> GetNotRolePermissions(Long roleId){
        Role role = roleRepository.findById(roleId).orElseGet(null);
        List<Privilege> rolePrivileges = (List)role.getPrivileges();
        List<Privilege> allPrivileges = privilegeRepository.findAll();
        List<Privilege> neededPrivileges = new ArrayList<>();
        for (Privilege privilege : allPrivileges) {
            if (!rolePrivileges.contains(privilege)) {
                neededPrivileges.add(privilege);
            }
        }

        return neededPrivileges;


    }
    public List<Privilege> GetRolePermissions(RolesDTO rolesDTO){
        System.out.println(rolesDTO);
        List<String> roles = rolesDTO.getRoles();
        List<Privilege> rolePrivilege = new ArrayList<>();
        for(String role:roles){
            System.out.println(role);
            Role r = roleRepository.findByName(role);
            rolePrivilege.addAll((List<Privilege>) r.getPrivileges());

        }
        System.out.println(rolePrivilege);


        return rolePrivilege;

    }

    public boolean CheckPermission(RolePrivilegeDTO rolePrivilegeDTO){
        Role role = roleRepository.findById(rolePrivilegeDTO.getRoleId()).orElseGet(null);
        List<Privilege> rolePrivileges = (List)role.getPrivileges();
        for(Privilege privilege:rolePrivileges){
            if(privilege.getName().contains(rolePrivilegeDTO.getPrivilegeName()))
                return true;
        }
        return false;

    }
}
