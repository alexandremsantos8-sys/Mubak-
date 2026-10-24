package com.Senai.Mubak.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "perfis")
public class Perfil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private boolean venda;
    private boolean compra;
    private boolean gerenciamentoProduto;
    private boolean administracao;

    public Long getId() { return id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public boolean isVenda() { return venda; }
    public void setVenda(boolean venda) { this.venda = venda; }
    public boolean isCompra() { return compra; }
    public void setCompra(boolean compra) { this.compra = compra; }
    public boolean isGerenciamentoProduto() { return gerenciamentoProduto; }
    public void setGerenciamentoProduto(boolean gerenciamentoProduto) { this.gerenciamentoProduto = gerenciamentoProduto; }
    public boolean isAdministracao() { return administracao; }
    public void setAdministracao(boolean administracao) { this.administracao = administracao; }
}