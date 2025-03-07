package com.isge.demo.service;

import com.isge.demo.entity.Plat;

import java.util.List;

public interface PlatService {
    Plat createPlat(Plat plat);
    List<Plat> allPlats();
    Plat readPlat(String id);
    Plat updatePlat(Plat plat);
    void deletePlat(String id);
}
