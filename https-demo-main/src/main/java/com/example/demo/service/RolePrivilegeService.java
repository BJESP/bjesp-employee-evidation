package com.example.demo.service;

import com.example.demo.dto.RolePrivilegeDTO;
import com.example.demo.model.Privilege;
import com.example.demo.model.Role;
import com.example.demo.repo.PrivilegeRepo;
import com.example.demo.repo.RoleRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        List<Privilege> privilegeListDto = rolePrivilegeDTO.getPrivileges();

        for(int i = 0; i<privilegesList.size(); i++) {
            for (Privilege privilegeDto : privilegeListDto) {
                if(privilegesList.get(i).getId() == privilegeDto.getId())
                    privilegesList.remove(i);
            }
        }
        roleRepository.save(role);


    }

    public void AddRolePermission(RolePrivilegeDTO rolePrivilegeDTO){
        Role role = GetRoleById(rolePrivilegeDTO.getRoleId());
        List<Privilege> privilegesList = (List)role.getPrivileges();
        List<Privilege> privilegeListDto = rolePrivilegeDTO.getPrivileges();
        if(privilegesList.isEmpty()){
            for (Privilege privilegeDto : privilegeListDto) {
            privilegesList.add(privilegeDto);
            }

        }
        else {
            for (int i = 0; i <= privilegesList.size(); i++) {
                for (Privilege privilegeDto : privilegeListDto) {
                    if (privilegesList.get(i).getId() == privilegeDto.getId()) {
                        i++;
                    } else {
                        privilegesList.add(i, privilegeDto);
                    }
                }
            }
        }
        roleRepository.save(role);
    }
}
