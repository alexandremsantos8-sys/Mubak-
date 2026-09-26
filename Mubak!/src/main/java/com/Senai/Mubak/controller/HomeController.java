package com.Senai.Mubak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.Senai.Mubak.repository.ProdutoRepository;

@Controller
public class HomeController {

    private final ProdutoRepository produtoRepository;

    public HomeController(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // A página inicial é pública e apenas encaminha para o template home.html.
    @GetMapping("/")
    public String inicio(Model model) {
        var produtosDisponiveis = produtoRepository.findAll().stream()
                .filter(produto -> produto.getEstoque() != null && produto.getEstoque() > 0)
                .toList();
        model.addAttribute("produtosCarrossel", produtosDisponiveis);
        return "home";
    }
}
