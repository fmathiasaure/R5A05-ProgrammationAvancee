package com.example.demo.Joueur;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.Equipe.Equipe;

public interface JoueurRepository extends JpaRepository<Joueur, Integer> {

    /** Les joueurs encore presents : ceux supprimes logiquement sont ecartes. */
    List<Joueur> findBySupprimeFalse();

    /** Les joueurs selectionnables d'une equipe : actifs et non supprimes. */
    List<Joueur> findByEquipeAndStatutAndSupprimeFalse(Equipe equipe, Statut statut);
}
