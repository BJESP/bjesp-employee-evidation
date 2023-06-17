package com.example.demo.security;

import com.example.demo.controller.EngineerController;
import com.example.demo.model.Project;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public class CustomPermissionEvaluator implements PermissionEvaluator {

    private Logger logger =  LogManager.getLogger(CustomPermissionEvaluator.class);

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        if ((authentication == null) || (targetDomainObject == null) || !(permission instanceof String)){
           logger.error("User didn't have permission to access this resource");
            return false;
        }
        String targetType = targetDomainObject.getClass().getSimpleName().toUpperCase();
        logger.info("User  has permission to access this resource");
        return hasPrivilege(authentication, targetType, permission.toString().toUpperCase());
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
            if ((authentication == null) || (targetType == null) || !(permission instanceof String)) {
                logger.error("User didn't have permission to access this resource");
                return false;
            }
        logger.info("User  has permission to access this resource");
            return hasPrivilege(authentication, targetType.toUpperCase(),
                    permission.toString().toUpperCase());
    }

    private boolean hasPrivilege(Authentication auth, String targetType, String permission) {


        for (GrantedAuthority grantedAuth : auth.getAuthorities()) {

            if (grantedAuth.getAuthority().startsWith(targetType)) {

                if (grantedAuth.getAuthority().contains(permission)) {
                    return true;
                }
            }
        }
        return false;
    }
}
