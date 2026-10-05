package com.example.demo.Joueur;

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

    @PostMapping("/joueur")
    @ResponseStatus(HttpStatus.CREATED)
    public Joueur creerJoueur(@RequestBody Joueur joueur) {
        return joueurRepository.save(joueur);
    }

    @PutMapping("/joueur")
    public ResponseEntity<Joueur> remplacerJoueur(@RequestParam Integer id, @RequestBody Joueur nouveau) {
        Optional<Joueur> trouve = joueurRepository.findById(id);
        if (trouve.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Joueur joueur = trouve.get();
        joueur.setNom(nouveau.getNom());
        joueur.setPrenom(nouveau.getPrenom());
        joueur.setDateNaissance(nouveau.getDateNaissance());
        joueur.setTaille(nouveau.getTaille());
        joueur.setPoids(nouveau.getPoids());
        joueur.setPoste(nouveau.getPoste());
        joueur.setNoteMoyenne(nouveau.getNoteMoyenne());
        joueur.setNbMatchsJoues(nouveau.getNbMatchsJoues());
        joueur.setStatut(nouveau.getStatut());
        joueur.setEquipe(nouveau.getEquipe());
        return ResponseEntity.ok(joueurRepository.save(joueur));
    }
    
    @PatchMapping("/joueur")
    public ResponseEntity<Joueur> modifierJoueur(@RequestParam Integer id, @RequestBody Joueur modifs) {
        Optional<Joueur> trouve = joueurRepository.findById(id);
        if (trouve.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Joueur joueur = trouve.get();
        if (modifs.getNom() != null) {
            joueur.setNom(modifs.getNom());
        }
        if (modifs.getPrenom() != null) {
            joueur.setPrenom(modifs.getPrenom());
        }
        if (modifs.getDateNaissance() != null) {
            joueur.setDateNaissance(modifs.getDateNaissance());
        }
        if (modifs.getTaille() != null) {
            joueur.setTaille(modifs.getTaille());
        }
        if (modifs.getPoids() != null) {
            joueur.setPoids(modifs.getPoids());
        }
        if (modifs.getPoste() != null) {
            joueur.setPoste(modifs.getPoste());
        }
        if (modifs.getNoteMoyenne() != null) {
            joueur.setNoteMoyenne(modifs.getNoteMoyenne());
        }
        if (modifs.getNbMatchsJoues() != null) {
            joueur.setNbMatchsJoues(modifs.getNbMatchsJoues());
        }
        if (modifs.getStatut() != null) {
            joueur.setStatut(modifs.getStatut());
        }
        if (modifs.getEquipe() != null) {
            joueur.setEquipe(modifs.getEquipe());
        }
        return ResponseEntity.ok(joueurRepository.save(joueur));
    }

    @DeleteMapping("/joueur")
    public ResponseEntity<Void> supprimerJoueur(@RequestParam Integer id) {
        if (!joueurRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        joueurRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
