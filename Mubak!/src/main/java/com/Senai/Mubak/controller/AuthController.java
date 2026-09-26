package com.Senai.Mubak.controller;

import com.Senai.Mubak.model.Usuario;
import com.Senai.Mubak.model.UsuarioForm;
import com.Senai.Mubak.service.UsuarioService;
import com.Senai.Mubak.config.AuthenticationInterceptor;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;
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
    public String cadastrar(@Valid @ModelAttribute UsuarioForm usuarioForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) return "auth/cadastro";
        try {
            usuarioService.cadastrar(usuarioForm);
            return "redirect:/cadastro/sucesso";
        } catch (Exception exception) {
            model.addAttribute("erro", exception.getMessage());
            return "auth/cadastro";
        }
    }

    @GetMapping("/cadastro/sucesso")
    public String cadastroSucesso() {
        return "auth/sucesso";
    }

    @GetMapping("/login")
    public String login(@RequestParam(required = false) String erro, Model model) {
        model.addAttribute("erro", erro);
        return "auth/login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String identificador, @RequestParam String senha,
                             HttpSession session, HttpServletRequest request) {
        try {
            Usuario usuario = usuarioService.login(identificador, senha);
            String destino = (String) session.getAttribute("redirectAfterLogin");
            session.removeAttribute("redirectAfterLogin");
            request.changeSessionId();
            session.setAttribute("usuarioId", usuario.getId());
            session.setAttribute("perfil", usuario.getPerfil());
            if (destinoValido(destino, usuario.getPerfil())) return "redirect:" + destino;
            return "redirect:/produtos";
        } catch (Exception exception) {
            return "redirect:/login?erro=Login+inválido";
        }
    }

    @PostMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }

    private boolean destinoValido(String destino, String perfil) {
        if (destino == null || !destino.startsWith("/") || destino.startsWith("//")
                || destino.contains("\\") || destino.startsWith("/login")
                || destino.startsWith("/cadastro") || destino.startsWith("/logout")) return false;
        String caminho = destino.split("\\?", 2)[0];
        boolean destinoAdmin = caminho.startsWith("/admin") || caminho.equals("/produtos/novo")
                || caminho.startsWith("/produtos/editar/");
        return !destinoAdmin || AuthenticationInterceptor.isAdminProfile(perfil);
    }
}
