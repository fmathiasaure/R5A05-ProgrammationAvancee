package com.example.demo.security;

import java.util.List;

/**
 * Reponse renvoyee apres une connexion reussie.
 * Record plutot que Lombok (@Getter / @AllArgsConstructor) : meme JSON, sans dependance supplementaire.
 */
public record JwtDTO(String token, String type, String username, List<String> roles) {

    public JwtDTO(String token, String username, List<String> roles) {
        this(token, "Bearer", username, roles);
    }
}
