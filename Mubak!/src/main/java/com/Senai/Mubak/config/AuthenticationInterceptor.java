package com.Senai.Mubak.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.regex.Pattern;

public class AuthenticationInterceptor implements HandlerInterceptor {
    private static final Pattern PRODUCT_DETAILS = Pattern.compile("/produtos/\\d+");
    private static final Pattern PRODUCT_MUTATION = Pattern.compile("/produtos(?:/\\d+(?:/excluir)?)?");
    private static final Pattern CART_ACTION = Pattern.compile("/carrinho/(?:itens|quantidade|remover)/\\d+");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        String method = request.getMethod();
        if (isPublicRequest(path, method)) return true;

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("usuarioId") == null) {
            if ("GET".equals(method) && !path.equals("/error")) {
                HttpSession loginSession = request.getSession(true);
                String destination = path.substring(request.getContextPath().length());
                if (request.getQueryString() != null) destination += "?" + request.getQueryString();
                loginSession.setAttribute("redirectAfterLogin", destination);
            }
            response.sendRedirect("/login");
            return false;
        }

        if (requiresAdmin(path, method) && !isAdminProfile(session.getAttribute("perfil"))) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }

    private boolean isPublicRequest(String path, String method) {
        if (path.startsWith("/css/") || path.startsWith("/js/") || path.startsWith("/images/")
                || path.startsWith("/uploads/") || path.startsWith("/webjars/") || path.equals("/error")) return true;
        if ("GET".equals(method)) {
            return path.equals("/") || path.equals("/produtos") || PRODUCT_DETAILS.matcher(path).matches()
                    || path.equals("/carrinho") || path.equals("/login") || path.equals("/cadastro")
                    || path.equals("/cadastro/sucesso");
        }
        return "POST".equals(method) && (path.equals("/login") || path.equals("/cadastro")
                || path.equals("/carrinho/esvaziar") || CART_ACTION.matcher(path).matches());
    }

    private boolean requiresAdmin(String path, String method) {
        if (path.startsWith("/admin")) return true;
        if (path.equals("/produtos/novo") || path.startsWith("/produtos/editar/")) return true;
        return PRODUCT_MUTATION.matcher(path).matches() && !"GET".equals(method);
    }

    public static boolean isAdminProfile(Object profile) {
        if (profile == null) return false;
        String normalized = profile.toString().trim();
        return "ROOT".equalsIgnoreCase(normalized) || "ADMIN".equalsIgnoreCase(normalized)
                || "ADMINISTRADOR".equalsIgnoreCase(normalized);
    }
}
