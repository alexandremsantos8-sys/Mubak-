package com.Senai.Mubak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import jakarta.servlet.http.HttpSession;
import com.Senai.Mubak.config.AuthenticationInterceptor;

import com.Senai.Mubak.repository.ProdutoRepository;

@Controller
public class HomeController {

    private final ProdutoRepository produtoRepository;

    public HomeController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // A página inicial é pública e apenas encaminha para o template home.html.
    @GetMapping("/")
    public String inicio(Model model, HttpSession session) {
        var produtosDisponiveis = produtoRepository.findAll().stream()
                .filter(produto -> produto.getEstoque() != null && produto.getEstoque() > 0)
                .toList();
        model.addAttribute("produtosCarrossel", produtosDisponiveis);
        model.addAttribute("admin", AuthenticationInterceptor.isRootProfile(session.getAttribute("perfil")));
        return "home";
    }
}
