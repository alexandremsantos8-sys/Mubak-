package com.Senai.Mubak.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    public produto salvar(produto produto) {
        validar(produto);
        return produtoRepository.save(produto);
    }

    public produto buscarPorId(Long id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado."));
    }

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

    public void excluir(Long id) {
        produtoRepository.delete(buscarPorId(id));
    }

    public List<produto> listar(String busca) {
        if (!StringUtils.hasText(busca)) {
            return produtoRepository.findAll();
        }
        return produtoRepository.findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCase(busca, busca);
    }

    public Optional<produto> buscarPorId(Long id) {
        return produtoRepository.findById(id);
    }

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