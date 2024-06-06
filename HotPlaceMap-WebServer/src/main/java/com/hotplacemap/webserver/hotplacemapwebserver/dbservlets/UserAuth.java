package com.hotplacemap.webserver.hotplacemapwebserver.dbservlets;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

public class UserAuth {
    public static boolean userIsAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SimpleGrantedAuthority adminAuthority = new SimpleGrantedAuthority("ROLE_ADMIN");
        return authentication != null && authentication.getAuthorities().contains(adminAuthority);
    }
}
