package com.isge.demo.repository;
import com.isge.demo.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlatRepository extends JpaRepository<Plat, String> {
	
}

