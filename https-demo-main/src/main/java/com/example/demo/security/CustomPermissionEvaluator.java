package com.example.demo.security;

import com.example.demo.model.Project;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public class CustomPermissionEvaluator implements PermissionEvaluator {


    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || (targetDomainObject == null) || !(permission instanceof String)){
            return false;
        }
        String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();

        return hasPrivilege(authentication, targetType, permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
            if ((authentication == null) || (targetType == null) || !(permission instanceof String)) {
                return false;
            }
            return hasPrivilege(authentication, targetType.toUpperCase(),
                    permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Authentication auth, String targetType, String permission) {
            System.out.println(targetType+"ovo je target type");
        System.out.println(permission+"ovo je permission");

        for (GrantedAuthority grantedAuth : auth.getAuthorities()) {
            System.out.println(grantedAuth+"ima ove permisije");
            if (grantedAuth.getAuthority().startsWith(targetType)) {
                System.out.println("dosaooooooo");
                if (grantedAuth.getAuthority().contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }
}
