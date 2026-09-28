package com.example.demo.Equipe;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@RestController
public class EquipeController {

    private final EquipeRepository equipeRepository;

    public EquipeController(EquipeRepository equipeRepository) {
        this.equipeRepository = equipeRepository;
    }

    @GetMapping("/equipe")
    public List<Equipe> tousLesEquipes() {
        return equipeRepository.findAll();
    }

    @GetMapping(value = "/equipe", params = "id")
    public ResponseEntity<Equipe> unEquipe(@RequestParam Integer id) {
        return ResponseEntity.of(equipeRepository.findById(id));
    }

    @PostMapping("/equipe")
    @ResponseStatus(HttpStatus.CREATED)
    public Equipe creerEquipe(@RequestBody Equipe equipe) {
        return equipeRepository.save(equipe);
    }
}