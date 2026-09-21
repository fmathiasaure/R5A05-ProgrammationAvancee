package com.example.demo;

import jakarta.persistence.*;

@Entity
@Table(name = "matchs")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe1_id")
    private Equipe equipe1;

    @ManyToOne(optional = false)
    @JoinColumn(name = "equipe2_id")
    private Equipe equipe2;

    // 1 = victoire, 2 = egalite, 3 = defaite (resultatEquipe1 + resultatEquipe2 = 4)
    private Integer resultatEquipe1;
    private Integer resultatEquipe2;

    private String adresseStade;
    private String nomArbitre;

    public Match() {}

    public Integer getId() { return id; }

    public Equipe getEquipe1() { return equipe1; }
    public void setEquipe1(Equipe equipe1) { this.equipe1 = equipe1; }

    public Equipe getEquipe2() { return equipe2; }
    public void setEquipe2(Equipe equipe2) { this.equipe2 = equipe2; }

    public Integer getResultatEquipe1() { return resultatEquipe1; }
    public void setResultatEquipe1(Integer resultatEquipe1) { this.resultatEquipe1 = resultatEquipe1; }

    public Integer getResultatEquipe2() { return resultatEquipe2; }
    public void setResultatEquipe2(Integer resultatEquipe2) { this.resultatEquipe2 = resultatEquipe2; }

    public String getAdresseStade() { return adresseStade; }
    public void setAdresseStade(String adresseStade) { this.adresseStade = adresseStade; }

    public String getNomArbitre() { return nomArbitre; }
    public void setNomArbitre(String nomArbitre) { this.nomArbitre = nomArbitre; }
}
