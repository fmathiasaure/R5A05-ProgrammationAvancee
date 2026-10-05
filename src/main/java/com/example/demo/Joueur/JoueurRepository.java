package com.example.demo.Joueur;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Equipe.Equipe;

public interface JoueurRepository extends JpaRepository<Joueur, Integer> {

    List<Joueur> findByEquipeAndStatut(Equipe equipe, Statut statut);
}
