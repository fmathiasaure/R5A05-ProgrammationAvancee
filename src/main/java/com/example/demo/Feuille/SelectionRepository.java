package com.example.demo.Feuille;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Joueur.Joueur;

public interface SelectionRepository extends JpaRepository<Selection, Integer> {

    List<Selection> findByFeuille(FeuilleMatch feuille);

    Optional<Selection> findByFeuilleAndJoueur(FeuilleMatch feuille, Joueur joueur);
}
