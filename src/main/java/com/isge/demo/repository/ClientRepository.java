package com.isge.demo.repository;


import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isge.demo.entity.Client;

@Repository
public interface ClientRepository extends JpaRepository<Client, String> {
	Optional<Client> findByEmail(String email);
}
