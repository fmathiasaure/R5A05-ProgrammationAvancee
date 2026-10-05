package com.example.demo.Feuille;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.Joueur.Joueur;

@RestController
public class FeuilleMatchController {

    private final FeuilleMatchService service;

    public FeuilleMatchController(FeuilleMatchService service) {
        this.service = service;
    }

    // GET /feuille : toutes les feuilles
    @GetMapping("/feuille")
    public List<FeuilleDetail> toutesLesFeuilles() {
        return service.toutesLesFeuilles();
    }

    // GET /feuille?id=1 : une feuille avec ses titulaires, ses remplacants et les erreurs restantes
    @GetMapping(value = "/feuille", params = "id")
    public FeuilleDetail uneFeuille(@RequestParam Integer id) {
        return service.detail(service.getFeuille(id));
    }

    // POST /feuille?matchId=1&equipeId=1 : cree une feuille vide
    @PostMapping("/feuille")
    @ResponseStatus(HttpStatus.CREATED)
    public FeuilleDetail creerFeuille(@RequestParam Integer matchId, @RequestParam Integer equipeId) {
        return service.detail(service.creer(matchId, equipeId));
    }

    // GET /feuille/disponibles?id=1 : joueurs actifs de l'equipe pas encore selectionnes
    @GetMapping("/feuille/disponibles")
    public List<Joueur> joueursDisponibles(@RequestParam Integer id) {
        return service.joueursDisponibles(id);
    }

    // POST /feuille/selection?id=1 avec {"joueurId":3,"poste":"MILIEU","titulaire":true}
    @PostMapping("/feuille/selection")
    public FeuilleDetail selectionner(@RequestParam Integer id, @RequestBody SelectionRequest demande) {
        service.selectionner(id, demande);
        return service.detail(service.getFeuille(id));
    }

    // DELETE /feuille/selection?id=1&joueurId=3 : retire un joueur de la feuille
    @DeleteMapping("/feuille/selection")
    public FeuilleDetail retirer(@RequestParam Integer id, @RequestParam Integer joueurId) {
        service.retirer(id, joueurId);
        return service.detail(service.getFeuille(id));
    }

    // GET /feuille/controle?id=1 : la feuille est-elle validable ?
    @GetMapping("/feuille/controle")
    public ControleResultat controler(@RequestParam Integer id) {
        List<String> erreurs = service.controler(service.getFeuille(id));
        return new ControleResultat(erreurs.isEmpty(), erreurs);
    }

    // POST /feuille/valider?id=1 : valide, ou 409 avec la liste des manques
    @PostMapping("/feuille/valider")
    public ResponseEntity<FeuilleDetail> valider(@RequestParam Integer id) {
        return ResponseEntity.ok(service.detail(service.valider(id)));
    }

    // POST /feuille/devalider?id=1 : rouvre une feuille validee
    @PostMapping("/feuille/devalider")
    public FeuilleDetail devalider(@RequestParam Integer id) {
        return service.detail(service.devalider(id));
    }
}
