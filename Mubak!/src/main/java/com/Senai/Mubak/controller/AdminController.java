package com.Senai.Mubak.controller;

import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.Senai.Mubak.model.Perfil;
import com.Senai.Mubak.service.PerfilService;
import com.Senai.Mubak.service.UsuarioService;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final UsuarioService usuarios;
    private final PerfilService perfis;

    public AdminController(UsuarioService usuarios, PerfilService perfis) {
        this.usuarios = usuarios;
        this.perfis = perfis;
    }

    @GetMapping
    public String inicio() { return "redirect:/admin/perfis"; }

    @GetMapping("/perfis")
    public String listarPerfis(Model model) { model.addAttribute("perfis", perfis.listar()); return "admin/perfis"; }

    @GetMapping("/perfis/novo")
    public String novoPerfil(Model model) { model.addAttribute("perfil", new Perfil()); return "admin/perfil-form"; }

    @GetMapping("/perfis/editar/{id}")
    public String editarPerfil(@PathVariable Long id, Model model) { model.addAttribute("perfil", perfis.buscar(id)); return "admin/perfil-form"; }

    @PostMapping("/perfis")
    public String salvarPerfil(@RequestParam(required = false) Long id, @RequestParam String nome,
            @RequestParam(defaultValue = "false") boolean venda, @RequestParam(defaultValue = "false") boolean compra,
            @RequestParam(defaultValue = "false") boolean gerenciamentoProduto,
            @RequestParam(defaultValue = "false") boolean administracao, RedirectAttributes attributes) {
        try {
            perfis.salvar(id, nome, venda, compra, gerenciamentoProduto, administracao);
            attributes.addFlashAttribute("sucesso", "Perfil salvo com sucesso.");
            return "redirect:/admin/perfis";
        } catch (IllegalArgumentException exception) {
            attributes.addFlashAttribute("erro", exception.getMessage());
            return "redirect:/admin/perfis";
        }
    }

    @GetMapping("/usuarios")
    public String listarUsuarios(Model model) { model.addAttribute("usuarios", usuarios.listar()); model.addAttribute("perfis", perfis.listar()); return "admin/usuarios"; }

    @GetMapping("/usuarios/{id}/editar")
    public String editarUsuario(@PathVariable Long id, Model model) {
        model.addAttribute("usuario", usuarios.buscar(id)); model.addAttribute("perfis", perfis.listar()); return "admin/usuario-form";
    }

    @PostMapping("/usuarios/{id}")
    public String salvarUsuario(@PathVariable Long id, @RequestParam String nome, @RequestParam String email,
            @RequestParam String documento, @RequestParam(required = false) String senha, @RequestParam String perfil,
            RedirectAttributes attributes) {
        try {
            usuarios.atualizar(id, nome, email, documento, senha, perfil);
            attributes.addFlashAttribute("sucesso", "Usuário atualizado com sucesso.");
        } catch (IllegalArgumentException exception) { attributes.addFlashAttribute("erro", exception.getMessage()); }
        return "redirect:/admin/usuarios";
    }
}
