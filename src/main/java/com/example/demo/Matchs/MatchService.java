package com.example.demo.Matchs;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Equipe.Equipe;
import com.example.demo.Equipe.EquipeRepository;

/** Cycle de vie des matchs et mise a jour automatique des bilans d'equipe. */
@Service
@Transactional
public class MatchService {

    private static final int VICTOIRE = 1;
    private static final int EGALITE = 2;
    private static final int DEFAITE = 3;

    private final MatchRepository matchRepository;
    private final EquipeRepository equipeRepository;

    public MatchService(MatchRepository matchRepository, EquipeRepository equipeRepository) {
        this.matchRepository = matchRepository;
        this.equipeRepository = equipeRepository;
    }

    public Match getMatch(Integer id) {
        return matchRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Match " + id + " introuvable."));
    }

    /** Un match se cree toujours PROGRAMME, sans resultat : il faut une date. */
    public Match creer(Match match) {
        if (match.getDatePrevue() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "La date prevue du match est obligatoire (format 2026-04-12T21:00:00).");
        }
        match.setStatut(StatutMatch.PROGRAMME);
        match.setResultatEquipe1(null);
        match.setResultatEquipe2(null);
        return matchRepository.save(match);
    }

    /**
     * Enregistre le resultat et passe le match a JOUE.
     * Le resultat de l'equipe 2 se deduit de celui de l'equipe 1 (somme = 4).
     * Les bilans des deux equipes sont ensuite recalcules.
     */
    public Match jouer(Integer id, Integer resultatEquipe1) {
        Match match = getMatch(id);

        if (match.getStatut() == StatutMatch.ANNULE) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ce match est annule : il ne peut pas etre joue.");
        }
        if (resultatEquipe1 == null || resultatEquipe1 < VICTOIRE || resultatEquipe1 > DEFAITE) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "resultatEquipe1 doit valoir 1 (victoire), 2 (egalite) ou 3 (defaite).");
        }

        match.setResultatEquipe1(resultatEquipe1);
        match.setResultatEquipe2(4 - resultatEquipe1);
        match.setStatut(StatutMatch.JOUE);
        matchRepository.save(match);

        recalculerCompteurs(match.getEquipe1());
        recalculerCompteurs(match.getEquipe2());

        return match;
    }

    /** Annule un match non joue. */
    public Match annuler(Integer id) {
        Match match = getMatch(id);
        if (match.estJoue()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ce match a deja ete joue : il ne peut plus etre annule.");
        }
        match.setStatut(StatutMatch.ANNULE);
        return matchRepository.save(match);
    }

    /** Un match joue fait partie de l'historique : sa suppression est refusee. */
    public void supprimer(Integer id) {
        Match match = getMatch(id);
        if (match.estJoue()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Impossible de supprimer un match deja joue.");
        }
        matchRepository.delete(match);
    }

    /**
     * Recompte victoires, egalites et defaites a partir de tous les matchs joues de l'equipe.
     * Recalculer plutot qu'incrementer evite les doubles comptages et prend en compte
     * une correction de resultat.
     */
    public Equipe recalculerCompteurs(Equipe equipe) {
        List<Match> matchs = matchRepository.findParEquipeEtStatut(equipe.getId(), StatutMatch.JOUE);

        int victoires = 0;
        int egalites = 0;
        int defaites = 0;

        for (Match match : matchs) {
            Integer resultat = equipe.getId().equals(match.getEquipe1().getId())
                    ? match.getResultatEquipe1()
                    : match.getResultatEquipe2();

            if (resultat == null) {
                continue;
            }
            switch (resultat) {
                case VICTOIRE -> victoires++;
                case EGALITE  -> egalites++;
                case DEFAITE  -> defaites++;
                default -> { }
            }
        }

        equipe.setNbVictoires(victoires);
        equipe.setNbEgalites(egalites);
        equipe.setNbDefaites(defaites);
        return equipeRepository.save(equipe);
    }

    /** Recalcule le bilan de toutes les equipes (utile apres une reprise de donnees). */
    public List<Equipe> recalculerToutesLesEquipes() {
        return equipeRepository.findAll().stream().map(this::recalculerCompteurs).toList();
    }
}
