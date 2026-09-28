package com.example.demo.Matchs;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MatchController {

    private final MatchRepository matchRepository;

    public MatchController(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @GetMapping("/joueur")
    public List<Match> tousLesMatchs() {
        return matchRepository.findAll();
    }

    @GetMapping(value = "/match", params = "id")
    public ResponseEntity<Match> unMatch(@RequestParam Integer id) {
        return ResponseEntity.of(matchRepository.findById(id));
    }
}