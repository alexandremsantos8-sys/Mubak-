package com.Senai.Mubak.model;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "produtos")
// Entidade JPA que representa o item exibido no catálogo e manipulado pelo CRUD.
public class produto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Informe o nome do produto.")
    @Size(max = 120, message = "O nome deve ter no máximo 120 caracteres.")
    @Column(nullable = false, length = 120)
    private String nome;

    @NotBlank(message = "Informe a descrição do produto.")
    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres.")
    @Column(nullable = false, length = 2000)
    private String descricao;

    @NotNull(message = "Informe o preço do produto.")
    @DecimalMin(value = "0.01", message = "O preço deve ser maior que zero.")
    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal preco;

    @NotNull(message = "Informe o estoque do produto.")
    @PositiveOrZero(message = "O estoque não pode ser negativo.")
    @Column(nullable = false)
    private Integer estoque;

    @Size(max = 500, message = "A URL deve ter no máximo 500 caracteres.")
    @Column(nullable = false, length = 500)
    private String imagemUrl;

    @NotBlank(message = "Informe a categoria.")
    @Size(max = 80, message = "A categoria deve ter no máximo 80 caracteres.")
    @Column(nullable = false, length = 80)
    private String categoria;

    @NotBlank(message = "Informe o vendedor.")
    @Size(max = 120, message = "O vendedor deve ter no máximo 120 caracteres.")
    @Column(nullable = false, length = 120)
    private String vendedor;

    public produto() {
    }

    public produto(String nome, String descricao, BigDecimal preco, Integer estoque, String imagemUrl, String categoria, String vendedor) {
        this.nome = nome;
        this.descricao = descricao;
        this.preco = preco;
        this.estoque = estoque;
        this.imagemUrl = imagemUrl;
        this.categoria = categoria;
        this.vendedor = vendedor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
    public BigDecimal getPreco() { return preco; }
    public void setPreco(BigDecimal preco) { this.preco = preco; }
    public Integer getEstoque() { return estoque; }
    public void setEstoque(Integer estoque) { this.estoque = estoque; }
    public String getImagemUrl() { return imagemUrl; }
    public void setImagemUrl(String imagemUrl) { this.imagemUrl = imagemUrl; }
    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public String getVendedor() { return vendedor; }
    public void setVendedor(String vendedor) { this.vendedor = vendedor; }
}
