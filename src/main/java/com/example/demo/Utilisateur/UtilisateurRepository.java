package com.example.demo.Utilisateur;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {

    Optional<Utilisateur> findByNomUtilisateur(String nomUtilisateur);

    boolean existsByNomUtilisateur(String nomUtilisateur);
}
