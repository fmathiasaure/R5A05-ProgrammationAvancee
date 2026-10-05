package com.example.demo.Feuille;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FeuilleMatchRepository extends JpaRepository<FeuilleMatch, Integer> {

    Optional<FeuilleMatch> findByMatch_IdAndEquipe_Id(Integer matchId, Integer equipeId);
}
