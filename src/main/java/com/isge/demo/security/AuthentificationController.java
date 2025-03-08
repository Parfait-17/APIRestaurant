package com.isge.demo.security;

import com.isge.demo.entity.Client;
import com.isge.demo.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthentificationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ClientRepository clientRepository;

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthentificationRequest authentificationRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authentificationRequest.getEmail(), authentificationRequest.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(authentificationRequest.getEmail());
        final String jwt = jwtUtil.generateToken(userDetails);

        return ResponseEntity.ok(new AuthentificationResponse(jwt));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Vérifie si l'utilisateur existe déjà
        if (clientRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body(new RegisterResponse("Erreur : L'email est déjà utilisé."));
        }
        String ROLE=registerRequest.getRole();
        String ROLED="ROLE_"+ROLE;

        // Crée un nouvel utilisateur
        Client client = new Client();
        client.setNom(registerRequest.getNom());
        client.setEmail(registerRequest.getEmail());
        client.setPassword(passwordEncoder.encode(registerRequest.getPassword())); // Hash le mot de passe
        client.setAdresse(registerRequest.getAdresse());
        client.setRole(ROLED); // Définit le rôle par défaut comme "CLIENT"

        // Sauvegarde l'utilisateur dans la base de données
        clientRepository.save(client);

        return ResponseEntity.ok(new RegisterResponse("Utilisateur enregistré avec succès !"));
    }
}