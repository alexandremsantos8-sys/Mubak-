package com.Senai.Mubak.service;

import java.util.List;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import com.Senai.Mubak.model.Perfil;
import com.Senai.Mubak.repository.PerfilRepository;

@Service
public class PerfilService {
    private final PerfilRepository repository;
    public PerfilService(PerfilRepository repository) { this.repository = repository; }

    @PostConstruct
    public void criarPerfisPadrao() {
        criarSeAusente("ADMINISTRADOR", true, true, true, false);
        criarSeAusente("SUPORTE", false, false, true, false);
        criarSeAusente("CLIENTE", false, true, false, false);
        criarSeAusente("VENDEDOR", true, false, true, false);
    }
    public List<Perfil> listar() { return repository.findAll(); }
    public Perfil buscarPorNome(String nome) { return repository.findByNomeIgnoreCase(nome).orElse(null); }
    public Perfil buscar(Long id) { return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("Perfil não encontrado.")); }
    public Perfil salvar(Long id, String nome, boolean venda, boolean compra, boolean gerenciamentoProduto, boolean administracao) {
        String nomeNormalizado = nome.trim().toUpperCase();
        if (nomeNormalizado.isBlank()) throw new IllegalArgumentException("Informe o nome do perfil.");
        Perfil perfil = id == null ? new Perfil() : buscar(id);
        repository.findByNomeIgnoreCase(nomeNormalizado).ifPresent(outro -> {
            if (!outro.getId().equals(perfil.getId())) throw new IllegalArgumentException("Já existe um perfil com esse nome.");
        });
        perfil.setNome(nomeNormalizado); perfil.setVenda(venda); perfil.setCompra(compra);
        perfil.setGerenciamentoProduto(gerenciamentoProduto); perfil.setAdministracao(administracao);
        return repository.save(perfil);
    }
    private void criarSeAusente(String nome, boolean venda, boolean compra, boolean gerenciamentoProduto, boolean administracao) {
        if (repository.findByNomeIgnoreCase(nome).isEmpty()) salvar(null, nome, venda, compra, gerenciamentoProduto, administracao);
    }
}