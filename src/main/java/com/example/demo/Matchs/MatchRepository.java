package com.example.demo.Matchs;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MatchRepository extends JpaRepository<Match, Integer> {

    /** Tous les matchs joues par une equipe, qu'elle ait recu ou soit allee a l'exterieur. */
    @Query("select m from Match m where m.statut = :statut "
         + "and (m.equipe1.id = :equipeId or m.equipe2.id = :equipeId)")
    List<Match> findParEquipeEtStatut(@Param("equipeId") Integer equipeId,
                                      @Param("statut") StatutMatch statut);
}
