package com.isge.demo.service.implementation;

import com.isge.demo.entity.Commande;
import com.isge.demo.repository.CommandeRepository;
import com.isge.demo.service.CommandeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CommandeServiceImpl implements CommandeService {

    @Autowired
    private CommandeRepository commandeRepository;

    @Override
    public Commande createCommande(Commande commande) {
        return commandeRepository.save(commande);
    }

    @Override
    public List<Commande> allCommandes() {
        return commandeRepository.findAll();
    }

    @Override
    public Commande readCommande(String id) {
        Optional<Commande> optionalCommande = commandeRepository.findById(id);
        return optionalCommande.orElse(null);
    }

    @Override
    public Commande updateCommande(Commande commande) {
        if (commandeRepository.existsById(commande.getId())) {
            return commandeRepository.save(commande);
        }
        return null; // Ou lever une exception si la commande n'existe pas
    }

    @Override
    public void deleteCommande(String id) {
        commandeRepository.deleteById(id);
    }
}