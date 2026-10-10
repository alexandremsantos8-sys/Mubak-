package com.Senai.Mubak.model;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/** DTO do formulário de cadastro; recebe confirmações antes de virar uma entidade Usuario. */
public class UsuarioForm {

    @NotBlank
    private String nome;

    @NotBlank
    private String documento;

    @NotBlank
    private String email;

    @NotBlank
    private String confirmacaoEmail;

    @NotBlank
    private String senha;

    @NotBlank
    private String confirmacaoSenha;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dataNascimento;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getConfirmacaoEmail() {
        return confirmacaoEmail;
    }

    public void setConfirmacaoEmail(String confirmacaoEmail) {
        this.confirmacaoEmail = confirmacaoEmail;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public String getConfirmacaoSenha() {
        return confirmacaoSenha;
    }

    public void setConfirmacaoSenha(String confirmacaoSenha) {
        this.confirmacaoSenha = confirmacaoSenha;
    }
}