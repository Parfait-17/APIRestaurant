package com.isge.demo.service;


import com.isge.demo.entity.Commande;

import java.util.List;

public interface CommandeService {
    Commande createCommande(Commande commande);
    List<Commande> allCommandes();
    Commande readCommande(String id);
    Commande updateCommande(Commande commande);
    void deleteCommande(String id);
}
