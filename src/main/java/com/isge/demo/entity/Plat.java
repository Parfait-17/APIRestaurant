package com.isge.demo.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Représente un plat dans le système.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Plat {
	    @Id
	    @GeneratedValue(strategy = GenerationType.UUID)
	    private String id;

	    private String nom;
	    private double prix;
	    private String description;
	    private String categorie;

	    @ElementCollection
	    private List<String> allergenes;

	    private boolean disponible;

}
