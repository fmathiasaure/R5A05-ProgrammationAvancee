package com.example.demo.Matchs;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Equipe.Equipe;

@RestController
public class MatchController {

    private final MatchRepository matchRepository;
    private final MatchService matchService;

    public MatchController(MatchRepository matchRepository, MatchService matchService) {
        this.matchRepository = matchRepository;
        this.matchService = matchService;
    }

    @GetMapping("/match")
    public List<Match> tousLesMatchs() {
        return matchRepository.findAll();
    }

    @GetMapping(value = "/match", params = "id")
    public ResponseEntity<Match> unMatch(@RequestParam Integer id) {
        return ResponseEntity.of(matchRepository.findById(id));
    }

    // POST /match : cree un match PROGRAMME, datePrevue obligatoire, sans resultat
    @PostMapping("/match")
    @ResponseStatus(HttpStatus.CREATED)
    public Match creerMatch(@RequestBody Match match) {
        return matchService.creer(match);
    }

    // POST /match/jouer?id=1&resultatEquipe1=1 : enregistre le resultat et met a jour les bilans
    @PostMapping("/match/jouer")
    public Match jouerMatch(@RequestParam Integer id, @RequestParam Integer resultatEquipe1) {
        return matchService.jouer(id, resultatEquipe1);
    }

    // POST /match/annuler?id=1
    @PostMapping("/match/annuler")
    public Match annulerMatch(@RequestParam Integer id) {
        return matchService.annuler(id);
    }

    // POST /match/recalculer : recompte les bilans de toutes les equipes
    @PostMapping("/match/recalculer")
    public List<Equipe> recalculer() {
        return matchService.recalculerToutesLesEquipes();
    }

    // PUT : remplace les champs modifiables. Le statut et les resultats restent pilotes
    // par /match/jouer et /match/annuler, pour ne pas desynchroniser les bilans d'equipe.
    @PutMapping("/match")
    public ResponseEntity<Match> remplacerMatch(@RequestParam Integer id, @RequestBody Match nouveau) {
        Optional<Match> trouve = matchRepository.findById(id);
        if (trouve.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Match match = trouve.get();
        match.setEquipe1(nouveau.getEquipe1());
        match.setEquipe2(nouveau.getEquipe2());
        match.setDatePrevue(nouveau.getDatePrevue());
        match.setAdresseStade(nouveau.getAdresseStade());
        match.setNomArbitre(nouveau.getNomArbitre());
        return ResponseEntity.ok(matchRepository.save(match));
    }

    // PATCH : ne modifie que les champs presents dans le JSON
    @PatchMapping("/match")
    public ResponseEntity<Match> modifierMatch(@RequestParam Integer id, @RequestBody Match modifs) {
        Optional<Match> trouve = matchRepository.findById(id);
        if (trouve.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Match match = trouve.get();
        if (modifs.getEquipe1() != null) {
            match.setEquipe1(modifs.getEquipe1());
        }
        if (modifs.getEquipe2() != null) {
            match.setEquipe2(modifs.getEquipe2());
        }
        if (modifs.getDatePrevue() != null) {
            match.setDatePrevue(modifs.getDatePrevue());
        }
        if (modifs.getAdresseStade() != null) {
            match.setAdresseStade(modifs.getAdresseStade());
        }
        if (modifs.getNomArbitre() != null) {
            match.setNomArbitre(modifs.getNomArbitre());
        }
        return ResponseEntity.ok(matchRepository.save(match));
    }

    // DELETE : refuse si le match a ete joue
    @DeleteMapping("/match")
    public ResponseEntity<Void> supprimerMatch(@RequestParam Integer id) {
        matchService.supprimer(id);
        return ResponseEntity.noContent().build();
    }
}
