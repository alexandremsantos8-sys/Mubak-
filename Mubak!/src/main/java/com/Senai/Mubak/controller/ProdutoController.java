package com.Senai.Mubak.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Senai.Mubak.model.produto;
import com.Senai.Mubak.service.ImagemService;
import com.Senai.Mubak.service.ProdutoService;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ImagemService imagemService;

    public ProdutoController(ProdutoService produtoService, ImagemService imagemService) {
        this.produtoService = produtoService;
        this.imagemService = imagemService;
    }

    // Abre o formulário vazio usado para cadastrar um novo produto.
    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new produto());
        return "produtos/form";
    }

    // Busca o produto antes de abrir o mesmo formulário em modo de edição.
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("produto", produtoService.buscarPorId(id));
            return "produtos/form";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/produtos";
        }
    }

    // Recebe campos e arquivo, salva a imagem e então persiste os dados do produto.
    @PostMapping
    public String salvar(@Valid @ModelAttribute produto produto, BindingResult bindingResult,
                         @RequestParam("imagem") MultipartFile imagem,
                         RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "produtos/form";
        }
        try {
            produto.setImagemUrl(imagemService.salvar(imagem));
            produtoService.salvar(produto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto cadastrado com sucesso.");
            return "redirect:/produtos";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/produtos/novo";
        }
    }

    // Monta a tela pública de detalhes de um produto específico.
    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("produto", produtoService.buscarPorId(id));
            return "produtos/detalhes";
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/produtos";
        }
    }

    // Atualiza os campos e mantém a imagem antiga quando nenhum novo arquivo é enviado.
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @Valid @ModelAttribute produto produto,
                            BindingResult bindingResult,
                            @RequestParam("imagem") MultipartFile imagem,
                            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            return "produtos/form";
        }
        try {
            produto produtoAtual = produtoService.buscarPorId(id);
            if (imagem != null && !imagem.isEmpty()) {
                produto.setImagemUrl(imagemService.salvar(imagem));
            } else {
                produto.setImagemUrl(produtoAtual.getImagemUrl());
            }
            produtoService.atualizar(id, produto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto atualizado com sucesso.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/produtos/editar/" + id;
        }
        return "redirect:/produtos";
    }

    // Exclui o produto e devolve o usuário ao catálogo com uma mensagem temporária.
    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto excluído com sucesso.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/produtos";
    }

    // Lista todos os produtos ou filtra por nome/categoria quando há um termo de busca.
    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("produtos", produtoService.listar(busca));
        model.addAttribute("busca", busca == null ? "" : busca);
        return "produtos/lista";
    }
}