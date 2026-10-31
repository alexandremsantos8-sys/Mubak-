package com.Senai.Mubak.config;

import com.Senai.Mubak.service.CartService;
import com.Senai.Mubak.config.AuthenticationInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CartModelAdvice {
    private final CartService cartService;

    public CartModelAdvice(CartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute("cartCount")
    public int cartCount(HttpServletRequest request) {
        return cartService.count(request.getSession(false));
    }

    @ModelAttribute("admin")
    public boolean admin(HttpServletRequest request) {
        var session = request.getSession(false);
        return session != null && AuthenticationInterceptor.isRootProfile(session.getAttribute("perfil"));
    }
}
