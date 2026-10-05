package com.example.demo.Feuille;

import java.util.List;

/** Vue complete d'une feuille de match renvoyee par l'API. */
public record FeuilleDetail(
        Integer id,
        Integer matchId,
        Integer equipeId,
        String equipeNom,
        boolean validee,
        List<SelectionVue> titulaires,
        List<SelectionVue> remplacants,
        List<String> erreurs) {
}
