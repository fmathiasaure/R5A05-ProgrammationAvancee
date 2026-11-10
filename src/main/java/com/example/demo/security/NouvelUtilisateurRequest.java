package com.example.demo.security;

import com.example.demo.Utilisateur.Role;

/** Corps JSON de POST /auth/utilisateur. Le mot de passe arrive en clair et est hashe avant stockage. */
public record NouvelUtilisateurRequest(String nomUtilisateur, String motDePasse, Role role) {
}
