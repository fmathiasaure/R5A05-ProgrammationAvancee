package com.example.demo.Utilisateur;

import jakarta.persistence.*;

/** Compte permettant de se connecter a l'application. */
@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(unique = true)
    private String nomUtilisateur;

    /** Hash BCrypt, jamais le mot de passe en clair. */
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    private Role role;

    public Utilisateur() {}

    public Utilisateur(String nomUtilisateur, String motDePasse, Role role) {
        this.nomUtilisateur = nomUtilisateur;
        this.motDePasse = motDePasse;
        this.role = role;
    }

    public Integer getId() { return id; }

    public String getNomUtilisateur() { return nomUtilisateur; }
    public void setNomUtilisateur(String nomUtilisateur) { this.nomUtilisateur = nomUtilisateur; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Utilisateur other)) return false;
        return id != null && id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return Utilisateur.class.hashCode();
    }

    // Le hash du mot de passe n'apparait volontairement pas
    @Override
    public String toString() {
        return "Utilisateur{id=" + id
                + ", nomUtilisateur='" + nomUtilisateur + '\''
                + ", role=" + role
                + '}';
    }
}
