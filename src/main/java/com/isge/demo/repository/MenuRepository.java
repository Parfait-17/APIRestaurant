package com.isge.demo.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.isge.demo.entity.Menu;

@Repository
public interface MenuRepository extends JpaRepository<Menu, String> {}
