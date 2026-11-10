package com.example.demo.security;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.Utilisateur.Utilisateur;
import com.example.demo.Utilisateur.UtilisateurRepository;

@RestController
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final TokenGenerator tokenGenerator;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(AuthenticationManager authenticationManager,
                          TokenGenerator tokenGenerator,
                          UtilisateurRepository utilisateurRepository,
                          PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.tokenGenerator = tokenGenerator;
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // POST /auth/login (public) : echange un nom d'utilisateur et un mot de passe contre un token
    @PostMapping("/auth/login")
    public JwtDTO login(@RequestBody LoginRequest demande) {
        if (demande.nomUtilisateur() == null || demande.motDePasse() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il faut fournir nomUtilisateur et motDePasse.");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(demande.nomUtilisateur(), demande.motDePasse()));
        } catch (AuthenticationException e) {
            // Meme message que le compte existe ou non, pour ne pas reveler les noms valides
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
                    "Nom d'utilisateur ou mot de passe incorrect.");
        }

        UserDetails utilisateur = (UserDetails) authentication.getPrincipal();
        List<String> roles = utilisateur.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return new JwtDTO(tokenGenerator.generateJwtToken(authentication), utilisateur.getUsername(), roles);
    }

    // POST /auth/utilisateur : creation d'un compte, reservee aux moderateurs
    @PostMapping("/auth/utilisateur")
    @PreAuthorize("hasRole('MODERATEUR')")
    @ResponseStatus(HttpStatus.CREATED)
    public UtilisateurVue creerUtilisateur(@RequestBody NouvelUtilisateurRequest demande) {
        if (demande.nomUtilisateur() == null || demande.nomUtilisateur().isBlank()
                || demande.motDePasse() == null || demande.motDePasse().isBlank()
                || demande.role() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Il faut fournir nomUtilisateur, motDePasse et role (MODERATEUR ou ENTRAINEUR).");
        }
        if (utilisateurRepository.existsByNomUtilisateur(demande.nomUtilisateur())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Le nom d'utilisateur " + demande.nomUtilisateur() + " est deja pris.");
        }

        Utilisateur utilisateur = new Utilisateur(
                demande.nomUtilisateur(),
                passwordEncoder.encode(demande.motDePasse()),
                demande.role());
        return UtilisateurVue.de(utilisateurRepository.save(utilisateur));
    }

    // GET /auth/moi : l'utilisateur correspondant au token envoye
    @GetMapping("/auth/moi")
    public UtilisateurVue moi(Authentication authentication) {
        return utilisateurRepository.findByNomUtilisateur(authentication.getName())
                .map(UtilisateurVue::de)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilisateur introuvable."));
    }
}
