package com.isge.demo.service.implementation;

import com.isge.demo.entity.Plat;
import com.isge.demo.repository.PlatRepository;
import com.isge.demo.service.PlatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlatServiceImpl implements PlatService {

    @Autowired
    private PlatRepository platRepository;

    @Override
    public Plat createPlat(Plat plat) {
        return platRepository.save(plat);
    }

    @Override
    public List<Plat> allPlats() {
        return platRepository.findAll();
    }

    @Override
    public Plat readPlat(String id) {
        Optional<Plat> optionalPlat = platRepository.findById(id);
        return optionalPlat.orElse(null);
    }

    @Override
    public Plat updatePlat(Plat plat) {
        if (platRepository.existsById(plat.getId())) {
            return platRepository.save(plat);
        }
        return null; // Ou lever une exception si le plat n'existe pas
    }

    @Override
    public void deletePlat(String id) {
        platRepository.deleteById(id);
    }
}