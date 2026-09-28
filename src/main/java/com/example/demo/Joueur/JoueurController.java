package com.example.demo.Joueur;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JoueurController {

    private final JoueurRepository joueurRepository;

    public JoueurController(JoueurRepository joueurRepository) {
        this.joueurRepository = joueurRepository;
    }

    @GetMapping("/joueur")
    public List<Joueur> tousLesJoueurs() {
        return joueurRepository.findAll();
    }

    @GetMapping(value = "/joueur", params = "id")
    public ResponseEntity<Joueur> unJoueur(@RequestParam Integer id) {
        return ResponseEntity.of(joueurRepository.findById(id));
    }
}
