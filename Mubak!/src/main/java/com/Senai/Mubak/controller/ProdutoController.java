package com.Senai.Mubak.controller;

import com.Senai.Mubak.model.produto;
import com.Senai.Mubak.service.ProdutoService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;

    public ProdutoController(ProdutoService produtoService) {
        this.produtoService = produtoService;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new produto());
        return "produtos/form";
    }

    @PostMapping
    public String salvar(@ModelAttribute produto produto, RedirectAttributes redirectAttributes) {
        try {
            produtoService.salvar(produto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso.");
            return "redirect:/produtos";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/produtos/novo";
        }
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("produtos", produtoService.listar(busca));
        model.addAttribute("busca", busca == null ? "" : busca);
        return "produtos/lista";
    }
}