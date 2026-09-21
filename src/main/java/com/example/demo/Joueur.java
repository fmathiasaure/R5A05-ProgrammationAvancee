package com.example.demo;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "joueur")
public class Joueur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;
    private String prenom;
    private LocalDate dateNaissance;
    private Integer taille;
    private BigDecimal poids;
    private String poste;
    private BigDecimal noteMoyenne;
    private Integer nbMatchsJoues;

    @ManyToOne
    @JoinColumn(name = "equipe_id")
    private Equipe equipe;

    public Joueur() {}

    public Integer getId() { return id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }

    public LocalDate getDateNaissance() { return dateNaissance; }
    public void setDateNaissance(LocalDate dateNaissance) { this.dateNaissance = dateNaissance; }

    public Integer getTaille() { return taille; }
    public void setTaille(Integer taille) { this.taille = taille; }

    public BigDecimal getPoids() { return poids; }
    public void setPoids(BigDecimal poids) { this.poids = poids; }

    public String getPoste() { return poste; }
    public void setPoste(String poste) { this.poste = poste; }

    public BigDecimal getNoteMoyenne() { return noteMoyenne; }
    public void setNoteMoyenne(BigDecimal noteMoyenne) { this.noteMoyenne = noteMoyenne; }

    public Integer getNbMatchsJoues() { return nbMatchsJoues; }
    public void setNbMatchsJoues(Integer nbMatchsJoues) { this.nbMatchsJoues = nbMatchsJoues; }

    public Equipe getEquipe() { return equipe; }
    public void setEquipe(Equipe equipe) { this.equipe = equipe; }
}