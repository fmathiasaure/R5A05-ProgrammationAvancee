package com.example.demo.Matchs;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
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

    @DeleteMapping("/match")
    public ResponseEntity<Void> supprimerMatch(@RequestParam Integer id) {
        if (!matchRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        matchRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}