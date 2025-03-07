package com.isge.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isge.demo.entity.Commande;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, String> {
	
}

