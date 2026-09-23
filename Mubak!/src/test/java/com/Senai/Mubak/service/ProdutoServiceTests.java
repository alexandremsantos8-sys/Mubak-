package com.Senai.Mubak.service;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.Senai.Mubak.model.produto;
import com.Senai.Mubak.repository.ProdutoRepository;

@ExtendWith(MockitoExtension.class)
// Testes unitários das regras de produto usando repository simulado, sem banco real.
class ProdutoServiceTests {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    @Test
    void deveSalvarProdutoValido() {
        produto produto = produtoValido();
        when(produtoRepository.save(produto)).thenReturn(produto);

        produto resultado = produtoService.salvar(produto);

        assertEquals(produto, resultado);
        verify(produtoRepository).save(produto);
    }

    @Test
    void deveRecusarProdutoComPrecoInvalido() {
        produto produto = produtoValido();
        produto.setPreco(BigDecimal.ZERO);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> produtoService.salvar(produto));

        assertEquals("O preço deve ser maior que zero.", exception.getMessage());
    }

    @Test
    void deveAtualizarProdutoExistente() {
        Long id = 1L;
        produto existente = produtoValido();
        produto dadosAtualizados = produtoValido();
        dadosAtualizados.setNome("Produto atualizado");
        dadosAtualizados.setEstoque(8);
        when(produtoRepository.findById(id)).thenReturn(Optional.of(existente));
        when(produtoRepository.save(existente)).thenReturn(existente);

        produto resultado = produtoService.atualizar(id, dadosAtualizados);

        assertEquals("Produto atualizado", resultado.getNome());
        assertEquals(8, resultado.getEstoque());
        verify(produtoRepository).save(existente);
    }

    @Test
    void deveExcluirProdutoExistente() {
        Long id = 1L;
        produto existente = produtoValido();
        when(produtoRepository.findById(id)).thenReturn(Optional.of(existente));

        produtoService.excluir(id);

        verify(produtoRepository).delete(existente);
    }

    private produto produtoValido() {
        return new produto(
                "Produto teste",
                "Descrição do produto teste",
                new BigDecimal("25.90"),
                10,
                "https://example.com/imagem.jpg",
                "Categoria teste",
                "Vendedor teste");
    }
}