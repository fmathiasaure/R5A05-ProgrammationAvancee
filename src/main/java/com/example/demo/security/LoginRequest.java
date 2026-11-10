package com.example.demo.security;

/** Corps JSON de POST /auth/login. */
public record LoginRequest(String nomUtilisateur, String motDePasse) {
}
