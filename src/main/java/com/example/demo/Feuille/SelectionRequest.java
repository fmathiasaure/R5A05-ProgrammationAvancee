package com.example.demo.Feuille;

/** Corps JSON attendu pour selectionner un joueur sur une feuille. */
public record SelectionRequest(Integer joueurId, Poste poste, Boolean titulaire) {
}
