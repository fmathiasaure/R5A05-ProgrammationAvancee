package com.example.demo.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.demo.Utilisateur.Utilisateur;
import com.example.demo.Utilisateur.UtilisateurRepository;

/** Charge un utilisateur depuis la base pour que Spring Security puisse verifier son mot de passe. */
@Service
public class R505UserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public R505UserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Utilisateur utilisateur = utilisateurRepository.findByNomUtilisateur(username)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur inconnu : " + username));

        // roles(...) ajoute le prefixe ROLE_ : MODERATEUR devient l'autorite ROLE_MODERATEUR
        return User.withUsername(utilisateur.getNomUtilisateur())
                .password(utilisateur.getMotDePasse())
                .roles(utilisateur.getRole().name())
                .build();
    }
}
