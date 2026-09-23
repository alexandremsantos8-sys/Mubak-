package com.Senai.Mubak.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.Senai.Mubak.model.produto;
import com.Senai.Mubak.repository.ProdutoRepository;

@Service
public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    // O service valida os dados antes de entregar a entidade ao repository.
    public produto salvar(produto produto) {
        validar(produto);
        return produtoRepository.save(produto);
    }

    // Busca centralizada: quando não encontra, todas as telas recebem a mesma mensagem.
    public produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    }

    // Copia os campos editáveis para a entidade já existente, preservando o ID do banco.
    public produto atualizar(Long id, produto dados) {
        validar(dados);
        produto produto = buscarPorId(id);
        produto.setNome(dados.getNome());
        produto.setDescricao(dados.getDescricao());
        produto.setPreco(dados.getPreco());
        produto.setEstoque(dados.getEstoque());
        produto.setImagemUrl(dados.getImagemUrl());
        produto.setCategoria(dados.getCategoria());
        produto.setVendedor(dados.getVendedor());
        return produtoRepository.save(produto);
    }

    // Força o flush para que a exclusão seja enviada ao banco imediatamente.
    public void excluir(Long id) {
        produto produto = buscarPorId(id);
        produtoRepository.delete(produto);
        produtoRepository.flush();
    }

    // Sem busca retorna tudo; com busca consulta nome e categoria ignorando maiúsculas/minúsculas.
    public List<produto> listar(String busca) {
        if (!StringUtils.hasText(busca)) {
            return produtoRepository.findAll();
        }
        return produtoRepository.findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCase(busca, busca);
    }

    // Regras de domínio que protegem a consistência mínima dos produtos.
    private void validar(produto produto) {
        if (!StringUtils.hasText(produto.getNome())) {
            throw new IllegalArgumentException("Informe o nome do produto.");
        }
        if (!StringUtils.hasText(produto.getDescricao())) {
            throw new IllegalArgumentException("Informe a descrição do produto.");
        }
        if (produto.getPreco() == null || produto.getPreco().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("O preço deve ser maior que zero.");
        }
        if (produto.getEstoque() == null || produto.getEstoque() < 0) {
            throw new IllegalArgumentException("O estoque não pode ser negativo.");
        }
        if (!StringUtils.hasText(produto.getImagemUrl())) {
            throw new IllegalArgumentException("Selecione uma imagem para o produto.");
        }
        if (!StringUtils.hasText(produto.getCategoria())) {
            throw new IllegalArgumentException("Informe a categoria.");
        }
        if (!StringUtils.hasText(produto.getVendedor())) {
            throw new IllegalArgumentException("Informe o vendedor.");
        }
    }
}