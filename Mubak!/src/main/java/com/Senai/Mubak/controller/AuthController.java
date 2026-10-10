package com.Senai.Mubak.controller;

import com.Senai.Mubak.model.Usuario;
import com.Senai.Mubak.model.UsuarioForm;
import com.Senai.Mubak.service.UsuarioService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UsuarioService usuarioService;

    public AuthController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping("/cadastro")
    public String cadastro(Model model) {
        model.addAttribute("usuarioForm", new UsuarioForm());
        return "auth/cadastro";
    }

    @PostMapping("/cadastro")
    public String cadastrar(@Valid @ModelAttribute UsuarioForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "auth/cadastro";
        }

        try {
            usuarioService.cadastrar(form);
            return "redirect:/login?cadastro=sucesso";
        } catch (RuntimeException exception) {
            model.addAttribute("erro", exception.getMessage());
            return "auth/cadastro";
        }
    }

    @GetMapping("/cadastro/sucesso")
    public String cadastroSucesso() {
        return "auth/sucesso";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String erro,
            @RequestParam(required = false) String cadastro, Model model) {
        model.addAttribute("erro", erro);
        if ("sucesso".equals(cadastro)) {
            model.addAttribute("mensagem", "Cadastro realizado. Entre com seus dados para continuar.");
        }
        return "auth/login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String identificador, @RequestParam String senha, HttpSession session) {
        try {
            Usuario usuario = usuarioService.login(identificador, senha);
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("perfil", usuario.getPerfil());
            return "redirect:/produtos";
        } catch (RuntimeException exception) {
            return "redirect:/login?erro=Login+inválido";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
