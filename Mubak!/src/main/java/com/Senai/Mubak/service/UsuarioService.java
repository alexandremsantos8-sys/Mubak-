package com.Senai.Mubak.service;

import java.time.LocalDate;
import java.util.List;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import com.Senai.Mubak.model.*;
import com.Senai.Mubak.repository.UsuarioRepository;

@Service
public class UsuarioService {
    final UsuarioRepository r;
    final SenhaService s;

    public UsuarioService(UsuarioRepository r, SenhaService s) { this.r = r; this.s = s; }

    @PostConstruct
    public void root() {
        if (r.findByEmailIgnoreCase("root@root.local").isEmpty()) rootUser("root", "root");
    }

    public Usuario rootUser(String n, String p) {
        Usuario u = new Usuario();
        u.setNome(n); u.setEmail(n + "@root.local"); u.setDocumento("00000000000");
        u.setDataNascimento(LocalDate.of(1970, 1, 1)); u.setPerfil("ROOT"); u.setSenhaHash(s.hash(p));
        return r.save(u);
    }

    public Usuario cadastrar(UsuarioForm f) {
        if (!DocumentoValidator.valido(f.getDocumento())) throw new IllegalArgumentException("CPF ou CNPJ inválido.");
        if (!f.getEmail().equalsIgnoreCase(f.getConfirmacaoEmail())) throw new IllegalArgumentException("Os e-mails não conferem.");
        if (!f.getSenha().equals(f.getConfirmacaoSenha())) throw new IllegalArgumentException("As senhas não conferem.");
        Usuario u = new Usuario();
        u.setNome(f.getNome().trim()); u.setDocumento(DocumentoValidator.digits(f.getDocumento()));
        u.setDataNascimento(f.getDataNascimento()); u.setEmail(f.getEmail().trim().toLowerCase());
        u.setPerfil("CLIENTE"); u.setSenhaHash(s.hash(f.getSenha()));
        return r.save(u);
    }

    public Usuario login(String identificador, String senha) {
        Usuario u = identificador.equalsIgnoreCase("root")
                ? r.findByEmailIgnoreCase("root@root.local").orElse(null)
                : identificador.contains("@") ? r.findByEmailIgnoreCase(identificador).orElse(null)
                : r.findByDocumento(DocumentoValidator.digits(identificador)).orElse(null);
        if (u == null || !s.hash(senha).equals(u.getSenhaHash())) throw new IllegalArgumentException();
        return u;
    }

    public List<Usuario> listar() { return r.findAll(); }
    public Usuario buscar(Long id) { return r.findById(id).orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado.")); }

    public Usuario atualizar(Long id, String nome, String email, String documento, String senha, String perfil) {
        Usuario u = buscar(id);
        if ("ROOT".equals(u.getPerfil()) && !"ROOT".equalsIgnoreCase(perfil)) {
            throw new IllegalArgumentException("O usuário root não pode perder esse perfil.");
        }
        if (!DocumentoValidator.valido(documento)) throw new IllegalArgumentException("CPF ou CNPJ inválido.");
        u.setNome(nome.trim()); u.setEmail(email.trim().toLowerCase());
        u.setDocumento(DocumentoValidator.digits(documento)); u.setPerfil(perfil.toUpperCase());
        if (senha != null && !senha.isBlank()) u.setSenhaHash(s.hash(senha));
        return r.save(u);
    }
}
