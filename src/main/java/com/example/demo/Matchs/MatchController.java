package com.example.demo.Matchs;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
public class MatchController {

    private final MatchRepository matchRepository;

    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @GetMapping("/match")
    public List<Match> tousLesMatchs() {
        return matchRepository.findAll();
    }

    @GetMapping(value = "/match", params = "id")
    public ResponseEntity<Match> unMatch(@RequestParam Integer id) {
        return ResponseEntity.of(matchRepository.findById(id));
    }

    @PostMapping("/match")
    @ResponseStatus(HttpStatus.CREATED)
    public Match creerMatch(@RequestBody Match match) {
        return matchRepository.save(match);
    }

    // PUT : remplace TOUS les champs du match
    @PutMapping("/match")
    public ResponseEntity<Match> remplacerMatch(@RequestParam Integer id, @RequestBody Match nouveau) {
        Optional<Match> trouve = matchRepository.findById(id);
        if (trouve.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Match match = trouve.get();
        match.setEquipe1(nouveau.getEquipe1());
        match.setEquipe2(nouveau.getEquipe2());
        match.setResultatEquipe1(nouveau.getResultatEquipe1());
        match.setResultatEquipe2(nouveau.getResultatEquipe2());
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
        if (modifs.getResultatEquipe1() != null) {
            match.setResultatEquipe1(modifs.getResultatEquipe1());
        }
        if (modifs.getResultatEquipe2() != null) {
            match.setResultatEquipe2(modifs.getResultatEquipe2());
        }
        if (modifs.getAdresseStade() != null) {
            match.setAdresseStade(modifs.getAdresseStade());
        }
        if (modifs.getNomArbitre() != null) {
            match.setNomArbitre(modifs.getNomArbitre());
        }
        return ResponseEntity.ok(matchRepository.save(match));
    }

    @DeleteMapping("/match")
    public ResponseEntity<Void> supprimerMatch(@RequestParam Integer id) {
        if (!matchRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        matchRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}