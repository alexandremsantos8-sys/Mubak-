package com.Senai.Mubak.controller;

import com.Senai.Mubak.service.CartService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/carrinho")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public String carrinho(HttpSession session, Model model) {
        CartService.Snapshot snapshot = cartService.snapshot(session);
        model.addAttribute("linhas", snapshot.lines());
        model.addAttribute("subtotal", snapshot.subtotal());
        model.addAttribute("quantidadeTotal", snapshot.count());
        return "carrinho";
    }

    @PostMapping("/itens/{id}")
    public String adicionar(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        try {
            cartService.add(session, id);
            redirect.addFlashAttribute("carrinhoMensagem", "Produto adicionado ao carrinho.");
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("erroCarrinho", exception.getMessage());
        }
        return "redirect:/produtos/" + id;
    }

    @PostMapping("/quantidade/{id}")
    public String quantidade(@PathVariable Long id, @RequestParam int quantidade,
                             HttpSession session, RedirectAttributes redirect) {
        try {
            cartService.setQuantity(session, id, quantidade);
        } catch (IllegalArgumentException exception) {
            redirect.addFlashAttribute("erroCarrinho", exception.getMessage());
        }
        return "redirect:/carrinho";
    }

    @PostMapping("/remover/{id}")
    public String remover(@PathVariable Long id, HttpSession session, RedirectAttributes redirect) {
        cartService.remove(session, id);
        redirect.addFlashAttribute("carrinhoMensagem", "Produto removido do carrinho.");
        return "redirect:/carrinho";
    }

    @PostMapping("/esvaziar")
    public String esvaziar(HttpSession session) {
        cartService.clear(session);
        return "redirect:/carrinho";
    }
}
