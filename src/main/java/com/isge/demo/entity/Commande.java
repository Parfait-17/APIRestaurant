package com.isge.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Représente une commande passée par un client.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Commande {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String date;

    @ManyToMany
    private List<Plat> plats;

    @Enumerated(EnumType.STRING)
    private StatutCommande statut;

    @ManyToOne
    private Client client;

    private double prixTotal;

	
}

/**
 * Enumération pour les statuts de commande.
 */
enum StatutCommande {
    EN_ATTENTE,
    EN_PREPARATION,
    PRETE,
    LIVREE
}
