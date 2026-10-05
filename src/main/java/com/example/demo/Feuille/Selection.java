package com.example.demo.Feuille;

import com.example.demo.Joueur.Joueur;

import jakarta.persistence.*;

/** Un joueur retenu sur une feuille de match, titulaire ou remplacant, a un poste donne. */
@Entity
@Table(name = "selection",
       uniqueConstraints = @UniqueConstraint(name = "uq_selection", columnNames = { "feuille_id", "joueur_id" }))
public class Selection {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "feuille_id")
    private FeuilleMatch feuille;

    @ManyToOne(optional = false)
    @JoinColumn(name = "joueur_id")
    private Joueur joueur;

    @Enumerated(EnumType.STRING)
    private Poste poste;

    private Boolean titulaire;

    public Selection() {}

    public Selection(FeuilleMatch feuille, Joueur joueur, Poste poste, Boolean titulaire) {
        this.feuille = feuille;
        this.joueur = joueur;
        this.poste = poste;
        this.titulaire = titulaire;
    }

    public Integer getId() { return id; }

    public FeuilleMatch getFeuille() { return feuille; }
    public void setFeuille(FeuilleMatch feuille) { this.feuille = feuille; }

    public Joueur getJoueur() { return joueur; }
    public void setJoueur(Joueur joueur) { this.joueur = joueur; }

    public Poste getPoste() { return poste; }
    public void setPoste(Poste poste) { this.poste = poste; }

    public Boolean getTitulaire() { return titulaire; }
    public void setTitulaire(Boolean titulaire) { this.titulaire = titulaire; }

    public boolean estTitulaire() { return Boolean.TRUE.equals(titulaire); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Selection other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Selection.class.hashCode();
    }

    @Override
    public String toString() {
        return "Selection{id=" + id
                + ", feuilleId=" + (feuille != null ? feuille.getId() : null)
                + ", joueurId=" + (joueur != null ? joueur.getId() : null)
                + ", poste=" + poste
                + ", titulaire=" + titulaire
                + '}';
    }
}
