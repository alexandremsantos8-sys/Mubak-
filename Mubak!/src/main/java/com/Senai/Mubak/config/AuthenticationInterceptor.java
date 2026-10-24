package com.Senai.Mubak.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

public class AuthenticationInterceptor implements HandlerInterceptor {

    public static boolean isRootProfile(Object perfil) {
        if (perfil == null) {
            return false;
        }
        String valor = perfil.toString();
        return "ROOT".equalsIgnoreCase(valor);
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getRequestURI();
        if (isPublicPath(uri)) {
            return true;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            response.sendRedirect("/login");
            return false;
        }

        if (uri.startsWith("/admin") && !isRootProfile(session.getAttribute("perfil"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }

        return true;
    }

    private static boolean isPublicPath(String uri) {
        return "/".equals(uri)
                || uri.equals("/login")
                || uri.startsWith("/cadastro")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/uploads/")
                || "/favicon.ico".equals(uri);
    }
}
