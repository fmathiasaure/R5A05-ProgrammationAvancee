package com.example.demo.Equipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface EquipeRepository extends JpaRepository<Equipe, Integer> {

    /** Les equipes encore actives : celles supprimees logiquement sont ecartees. */
    List<Equipe> findBySupprimeFalse();
}
