package com.Senai.Mubak.repository;

import com.Senai.Mubak.model.produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<produto, Long> {

    List<produto> findByNomeContainingIgnoreCaseOrCategoriaContainingIgnoreCase(
            String nome, String categoria);
}