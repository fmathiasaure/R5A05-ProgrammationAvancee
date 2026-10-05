package com.example.demo.Feuille;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Equipe.Equipe;
import com.example.demo.Equipe.EquipeRepository;
import com.example.demo.Joueur.Joueur;
import com.example.demo.Joueur.JoueurRepository;
import com.example.demo.Joueur.Statut;
import com.example.demo.Matchs.Match;
import com.example.demo.Matchs.MatchRepository;

/** Regles metier des feuilles de match : selection des joueurs et controle avant validation. */
@Service
@Transactional
public class FeuilleMatchService {

    /** Formation 4-3-3 : le detail par poste est porte par l'enum Poste. */
    public static final int NB_TITULAIRES = 11;
    public static final int NB_REMPLACANTS = 7;

    private final FeuilleMatchRepository feuilleRepository;
    private final SelectionRepository selectionRepository;
    private final JoueurRepository joueurRepository;
    private final EquipeRepository equipeRepository;
    private final MatchRepository matchRepository;

    public FeuilleMatchService(FeuilleMatchRepository feuilleRepository,
                               SelectionRepository selectionRepository,
                               JoueurRepository joueurRepository,
                               EquipeRepository equipeRepository,
                               MatchRepository matchRepository) {
        this.feuilleRepository = feuilleRepository;
        this.selectionRepository = selectionRepository;
        this.joueurRepository = joueurRepository;
        this.equipeRepository = equipeRepository;
        this.matchRepository = matchRepository;
    }

    // ---------------------------------------------------------------- lecture

    public FeuilleMatch getFeuille(Integer feuilleId) {
        return feuilleRepository.findById(feuilleId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Feuille de match " + feuilleId + " introuvable."));
    }

    public List<FeuilleDetail> toutesLesFeuilles() {
        return feuilleRepository.findAll().stream().map(this::detail).toList();
    }

    /** Les joueurs actifs de l'equipe qui ne sont pas encore sur la feuille. */
    public List<Joueur> joueursDisponibles(Integer feuilleId) {
        FeuilleMatch feuille = getFeuille(feuilleId);
        List<Selection> deja = selectionRepository.findByFeuille(feuille);
        List<Integer> dejaPris = deja.stream().map(s -> s.getJoueur().getId()).toList();

        return joueurRepository.findByEquipeAndStatut(feuille.getEquipe(), Statut.ACTIF).stream()
                .filter(j -> !dejaPris.contains(j.getId()))
                .toList();
    }

    // ---------------------------------------------------------------- ecriture

    /** Cree la feuille d'une equipe pour un match. Une seule feuille par couple (match, equipe). */
    public FeuilleMatch creer(Integer matchId, Integer equipeId) {
        Match match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Match " + matchId + " introuvable."));
        Equipe equipe = equipeRepository.findById(equipeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Equipe " + equipeId + " introuvable."));

        boolean equipeDuMatch = equipe.getId().equals(match.getEquipe1().getId())
                || equipe.getId().equals(match.getEquipe2().getId());
        if (!equipeDuMatch) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "L'equipe " + equipe.getNom() + " ne joue pas ce match.");
        }

        if (feuilleRepository.findByMatch_IdAndEquipe_Id(matchId, equipeId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Une feuille existe deja pour cette equipe sur ce match.");
        }

        return feuilleRepository.save(new FeuilleMatch(match, equipe));
    }

    /** Ajoute un joueur a la feuille, en titulaire ou en remplacant. */
    public Selection selectionner(Integer feuilleId, SelectionRequest demande) {
        FeuilleMatch feuille = getFeuille(feuilleId);
        verifierModifiable(feuille);

        if (demande.joueurId() == null || demande.poste() == null || demande.titulaire() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il faut fournir joueurId, poste et titulaire.");
        }

        Joueur joueur = joueurRepository.findById(demande.joueurId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Joueur " + demande.joueurId() + " introuvable."));

        if (joueur.getEquipe() == null || !joueur.getEquipe().getId().equals(feuille.getEquipe().getId())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    joueur.getPrenom() + " " + joueur.getNom() + " ne fait pas partie de "
                            + feuille.getEquipe().getNom() + ".");
        }

        if (joueur.getStatut() != Statut.ACTIF) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    joueur.getPrenom() + " " + joueur.getNom() + " n'est pas actif (statut "
                            + joueur.getStatut() + ").");
        }

        if (selectionRepository.findByFeuilleAndJoueur(feuille, joueur).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    joueur.getPrenom() + " " + joueur.getNom() + " est deja sur la feuille.");
        }

        List<Selection> selections = selectionRepository.findByFeuille(feuille);

        if (Boolean.TRUE.equals(demande.titulaire())) {
            long dejaAuPoste = selections.stream()
                    .filter(Selection::estTitulaire)
                    .filter(s -> s.getPoste() == demande.poste())
                    .count();
            if (dejaAuPoste >= demande.poste().getNbTitulaires()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Formation 4-3-3 : il y a deja " + demande.poste().getNbTitulaires()
                                + " titulaire(s) au poste " + demande.poste() + ".");
            }
        } else {
            long nbRemplacants = selections.stream().filter(s -> !s.estTitulaire()).count();
            if (nbRemplacants >= NB_REMPLACANTS) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Le banc est complet (" + NB_REMPLACANTS + " remplacants).");
            }
        }

        return selectionRepository.save(new Selection(feuille, joueur, demande.poste(), demande.titulaire()));
    }

    /** Retire un joueur de la feuille. */
    public void retirer(Integer feuilleId, Integer joueurId) {
        FeuilleMatch feuille = getFeuille(feuilleId);
        verifierModifiable(feuille);

        Joueur joueur = joueurRepository.findById(joueurId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Joueur " + joueurId + " introuvable."));

        Optional<Selection> selection = selectionRepository.findByFeuilleAndJoueur(feuille, joueur);
        if (selection.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Ce joueur n'est pas sur la feuille.");
        }
        selectionRepository.delete(selection.get());
    }

    // ---------------------------------------------------------------- controle

    /** Liste les raisons pour lesquelles la feuille ne peut pas etre validee. Vide = feuille valide. */
    public List<String> controler(FeuilleMatch feuille) {
        List<String> erreurs = new ArrayList<>();
        List<Selection> selections = selectionRepository.findByFeuille(feuille);

        List<Selection> titulaires = selections.stream().filter(Selection::estTitulaire).toList();
        List<Selection> remplacants = selections.stream().filter(s -> !s.estTitulaire()).toList();

        if (titulaires.size() != NB_TITULAIRES) {
            erreurs.add("Il faut " + NB_TITULAIRES + " titulaires (actuellement " + titulaires.size() + ").");
        }
        if (remplacants.size() != NB_REMPLACANTS) {
            erreurs.add("Il faut " + NB_REMPLACANTS + " remplacants (actuellement " + remplacants.size() + ").");
        }

        for (Poste poste : Poste.values()) {
            long compte = titulaires.stream().filter(s -> s.getPoste() == poste).count();
            if (compte != poste.getNbTitulaires()) {
                erreurs.add("Formation 4-3-3 : " + poste.getNbTitulaires() + " " + poste
                        + " attendu(s) parmi les titulaires, " + compte + " selectionne(s).");
            }
        }

        for (Selection s : selections) {
            if (s.getJoueur().getStatut() != Statut.ACTIF) {
                erreurs.add(s.getJoueur().getPrenom() + " " + s.getJoueur().getNom()
                        + " n'est plus actif (statut " + s.getJoueur().getStatut() + ").");
            }
        }

        return erreurs;
    }

    /** Valide la feuille si toutes les regles sont respectees, sinon leve un 409. */
    public FeuilleMatch valider(Integer feuilleId) {
        FeuilleMatch feuille = getFeuille(feuilleId);

        if (feuille.estValidee()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Cette feuille est deja validee.");
        }

        List<String> erreurs = controler(feuille);
        if (!erreurs.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Validation impossible : " + String.join(" ", erreurs));
        }

        feuille.setValidee(true);
        return feuilleRepository.save(feuille);
    }

    /** Rouvre une feuille validee pour pouvoir la corriger. */
    public FeuilleMatch devalider(Integer feuilleId) {
        FeuilleMatch feuille = getFeuille(feuilleId);
        feuille.setValidee(false);
        return feuilleRepository.save(feuille);
    }

    // ---------------------------------------------------------------- vues

    public FeuilleDetail detail(FeuilleMatch feuille) {
        List<Selection> selections = selectionRepository.findByFeuille(feuille);

        List<SelectionVue> titulaires = selections.stream()
                .filter(Selection::estTitulaire)
                .map(this::vue)
                .toList();

        List<SelectionVue> remplacants = selections.stream()
                .filter(s -> !s.estTitulaire())
                .map(this::vue)
                .toList();

        return new FeuilleDetail(
                feuille.getId(),
                feuille.getMatch().getId(),
                feuille.getEquipe().getId(),
                feuille.getEquipe().getNom(),
                feuille.estValidee(),
                titulaires,
                remplacants,
                controler(feuille));
    }

    private SelectionVue vue(Selection s) {
        return new SelectionVue(s.getJoueur().getId(), s.getJoueur().getNom(),
                s.getJoueur().getPrenom(), s.getPoste());
    }

    private void verifierModifiable(FeuilleMatch feuille) {
        if (feuille.estValidee()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Feuille deja validee : la devalider avant de la modifier.");
        }
    }
}
