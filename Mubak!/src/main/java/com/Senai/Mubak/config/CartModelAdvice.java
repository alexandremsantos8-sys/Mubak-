package com.Senai.Mubak.config;

import com.Senai.Mubak.service.CartService;
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
}
