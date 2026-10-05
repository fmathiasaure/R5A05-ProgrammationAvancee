package com.example.demo.Feuille;

import java.util.List;

/** Resultat du controle d'une feuille : valide ou liste des raisons du refus. */
public record ControleResultat(boolean valide, List<String> erreurs) {
}
