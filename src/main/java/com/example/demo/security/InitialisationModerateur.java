package com.example.demo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.example.demo.Utilisateur.Role;
import com.example.demo.Utilisateur.Utilisateur;
import com.example.demo.Utilisateur.UtilisateurRepository;

/**
 * Cree le premier moderateur au demarrage, a partir de application.properties.
 * Sans lui, personne ne pourrait se connecter, et donc personne ne pourrait creer de compte.
 */
@Component
public class InitialisationModerateur implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(InitialisationModerateur.class);

    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${r5a05.app.moderateur.nom:}")
    private String nom;

    @Value("${r5a05.app.moderateur.motDePasse:}")
    private String motDePasse;

    public InitialisationModerateur(UtilisateurRepository utilisateurRepository, PasswordEncoder passwordEncoder) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (nom.isBlank() || motDePasse.isBlank()) {
            logger.warn("Aucun moderateur initial configure (r5a05.app.moderateur.nom / motDePasse).");
            return;
        }
        if (utilisateurRepository.existsByNomUtilisateur(nom)) {
            return;
        }
        utilisateurRepository.save(new Utilisateur(nom, passwordEncoder.encode(motDePasse), Role.MODERATEUR));
        logger.info("Moderateur initial cree : {}", nom);
    }
}
