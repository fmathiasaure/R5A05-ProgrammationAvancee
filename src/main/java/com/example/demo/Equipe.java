package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "equipe")
public class Equipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String nom;
    private String ville;
    private Integer nbVictoires;
    private Integer nbEgalites;
    private Integer nbDefaites;

    public Equipe() {}

    public Integer getId() { return id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getVille() { return ville; }
    public void setVille(String ville) { this.ville = ville; }

    public Integer getNbVictoires() { return nbVictoires; }
    public void setNbVictoires(Integer nbVictoires) { this.nbVictoires = nbVictoires; }

    public Integer getNbEgalites() { return nbEgalites; }
    public void setNbEgalites(Integer nbEgalites) { this.nbEgalites = nbEgalites; }

    public Integer getNbDefaites() { return nbDefaites; }
    public void setNbDefaites(Integer nbDefaites) { this.nbDefaites = nbDefaites; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Equipe other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Equipe.class.hashCode();
    }

    @Override
    public String toString() {
        return "Equipe{id=" + id
                + ", nom='" + nom + '\''
                + ", ville='" + ville + '\''
                + ", nbVictoires=" + nbVictoires
                + ", nbEgalites=" + nbEgalites
                + ", nbDefaites=" + nbDefaites
                + '}';
    }
}
