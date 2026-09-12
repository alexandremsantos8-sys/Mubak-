package com.Senai.Mubak.service;

import com.Senai.Mubak.model.produto;
import com.Senai.Mubak.repository.ProdutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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