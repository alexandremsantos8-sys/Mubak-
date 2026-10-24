package com.Senai.Mubak.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.Senai.Mubak.model.Perfil;

public interface PerfilRepository extends JpaRepository<Perfil, Long> {
    Optional<Perfil> findByNomeIgnoreCase(String nome);
}