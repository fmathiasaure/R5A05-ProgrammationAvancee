package com.example.demo.Equipe;

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
import org.springframework.dao.DataIntegrityViolationException;

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

    // PUT : remplace TOUS les champs de l'equipe
    @PutMapping("/equipe")
    public ResponseEntity<Equipe> remplacerEquipe(@RequestParam Integer id, @RequestBody Equipe nouvelle) {
        Optional<Equipe> trouve = equipeRepository.findById(id);
        if (trouve.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Equipe equipe = trouve.get();
        equipe.setNom(nouvelle.getNom());
        equipe.setVille(nouvelle.getVille());
        equipe.setNbVictoires(nouvelle.getNbVictoires());
        equipe.setNbEgalites(nouvelle.getNbEgalites());
        equipe.setNbDefaites(nouvelle.getNbDefaites());
        return ResponseEntity.ok(equipeRepository.save(equipe));
    }

    // PATCH : ne modifie que les champs presents dans le JSON
    @PatchMapping("/equipe")
    public ResponseEntity<Equipe> modifierEquipe(@RequestParam Integer id, @RequestBody Equipe modifs) {
        Optional<Equipe> trouve = equipeRepository.findById(id);
        if (trouve.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Equipe equipe = trouve.get();
        if (modifs.getNom() != null) {
            equipe.setNom(modifs.getNom());
        }
        if (modifs.getVille() != null) {
            equipe.setVille(modifs.getVille());
        }
        if (modifs.getNbVictoires() != null) {
            equipe.setNbVictoires(modifs.getNbVictoires());
        }
        if (modifs.getNbEgalites() != null) {
            equipe.setNbEgalites(modifs.getNbEgalites());
        }
        if (modifs.getNbDefaites() != null) {
            equipe.setNbDefaites(modifs.getNbDefaites());
        }
        return ResponseEntity.ok(equipeRepository.save(equipe));
    }

    @DeleteMapping("/equipe")
    public ResponseEntity<String> supprimerEquipe(@RequestParam Integer id) {
        if (!equipeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        try {
            equipeRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Impossible de supprimer cette equipe : elle a des matchs enregistres.");
        }
    }
}