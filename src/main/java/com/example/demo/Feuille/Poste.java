package com.example.demo.Feuille;

/**
 * Postes utilisables sur une feuille de match.
 * Le nombre de titulaires attendu correspond a la formation 4-3-3 (1 + 4 + 3 + 3 = 11).
 */
public enum Poste {

    GARDIEN(1),
    DEFENSEUR(4),
    MILIEU(3),
    ATTAQUANT(3);

    private final int nbTitulaires;

    Poste(int nbTitulaires) {
        this.nbTitulaires = nbTitulaires;
    }

    public int getNbTitulaires() {
        return nbTitulaires;
    }
}
