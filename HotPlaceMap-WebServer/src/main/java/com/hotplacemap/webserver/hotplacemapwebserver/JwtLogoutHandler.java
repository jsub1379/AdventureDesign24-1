package com.hotplacemap.webserver.hotplacemapwebserver;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtLogoutHandler implements LogoutHandler {
    private final JwtUtil jwtUtil;

    public JwtLogoutHandler(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String tokenHeader = request.getHeader("Authorization");
        if (tokenHeader != null && tokenHeader.startsWith("Bearer ")) {
            String token = tokenHeader.substring(7);
            jwtUtil.invalidateToken(token);
            try {
                response.setContentType("text/plain; charset=UTF-8");
                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().write("로그아웃 되었습니다!");
                response.getWriter().flush();
                response.getWriter().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
