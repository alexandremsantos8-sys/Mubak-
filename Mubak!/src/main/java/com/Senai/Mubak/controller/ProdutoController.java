package com.Senai.Mubak.controller;

<<<<<<< HEAD
import com.Senai.Mubak.model.produto;
import com.Senai.Mubak.service.ImagemService;
import com.Senai.Mubak.service.ProdutoService;
=======
>>>>>>> dde1454008ef259cd4dd14280c3ec8c0fb8eb6cb
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.Senai.Mubak.model.produto;
import com.Senai.Mubak.service.ProdutoService;

@Controller
@RequestMapping("/produtos")
public class ProdutoController {

    private final ProdutoService produtoService;
    private final ImagemService imagemService;

    public ProdutoController(ProdutoService produtoService, ImagemService imagemService) {
        this.produtoService = produtoService;
        this.imagemService = imagemService;
    }

    @GetMapping("/novo")
    public String novo(Model model) {
        model.addAttribute("produto", new produto());
        return "produtos/form";
    }

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

    @PostMapping
    public String salvar(@ModelAttribute produto produto,
                         @RequestParam(value = "imagem", required = false) MultipartFile imagem,
                         RedirectAttributes redirectAttributes) {
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

<<<<<<< HEAD
    @GetMapping("/{id}")
    public String detalhes(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        return produtoService.buscarPorId(id)
                .map(produto -> {
                    model.addAttribute("produto", produto);
                    return "produtos/detalhes";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("erro", "Produto não encontrado.");
                    return "redirect:/produtos";
                });
=======
    @PostMapping("/{id}")
    public String atualizar(@PathVariable Long id, @ModelAttribute produto produto,
                            RedirectAttributes redirectAttributes) {
        try {
            produtoService.atualizar(id, produto);
            redirectAttributes.addFlashAttribute("sucesso", "Produto atualizado com sucesso.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/produtos/editar/" + id;
        }
        return "redirect:/produtos";
    }

    @PostMapping("/{id}/excluir")
    public String excluir(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            produtoService.excluir(id);
            redirectAttributes.addFlashAttribute("sucesso", "Produto excluído com sucesso.");
        } catch (IllegalArgumentException exception) {
            redirectAttributes.addFlashAttribute("erro", exception.getMessage());
        }
        return "redirect:/produtos";
>>>>>>> dde1454008ef259cd4dd14280c3ec8c0fb8eb6cb
    }

    @GetMapping
    public String listar(@RequestParam(required = false) String busca, Model model) {
        model.addAttribute("produtos", produtoService.listar(busca));
        model.addAttribute("busca", busca == null ? "" : busca);
        return "produtos/lista";
    }
}