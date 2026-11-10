package com.example.demo.security;

import com.example.demo.Utilisateur.Role;
import com.example.demo.Utilisateur.Utilisateur;

/** Utilisateur tel que renvoye par l'API : sans le hash du mot de passe. */
public record UtilisateurVue(Integer id, String nomUtilisateur, Role role) {

    public static UtilisateurVue de(Utilisateur utilisateur) {
        return new UtilisateurVue(utilisateur.getId(), utilisateur.getNomUtilisateur(), utilisateur.getRole());
    }
}
