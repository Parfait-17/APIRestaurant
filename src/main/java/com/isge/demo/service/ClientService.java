package com.isge.demo.service;

import com.isge.demo.entity.Client;

import java.util.List;

public interface ClientService {
    Client createClient(Client client);
    List<Client> allClients();
    Client readClient(String id);
    Client updateClient(Client client);
    void deleteClient(String id);
}