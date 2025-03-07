package com.isge.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Représente un menu contenant plusieurs plats.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Menu {

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String nom;
    private String description;

    @ManyToMany
    private List<Plat> plats;

    private double prix;
}
