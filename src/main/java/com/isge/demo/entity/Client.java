package com.isge.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nom;
    private String email;
    private String password; // Ajoutez ce champ pour stocker le mot de passe
    private String role; // ROLE_ADMIN ou ROLE_CLIENT
    private String adresse;

    @OneToMany(mappedBy = "client")
    private List<Commande> historiqueCommandes;
    
 // Méthode pour les rôles (authorities)
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
    }
}