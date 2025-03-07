package com.isge.demo.service.implementation;

import com.isge.demo.entity.Client;
import com.isge.demo.repository.ClientRepository;
import com.isge.demo.service.ClientService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ClientServiceImpl implements ClientService {

    @Autowired
    private ClientRepository clientRepository;

    @Override
    public Client createClient(Client client) {
        return clientRepository.save(client);
    }

    @Override
    public List<Client> allClients() {
        return clientRepository.findAll();
    }

    @Override
    public Client readClient(String id) {
        Optional<Client> optionalClient = clientRepository.findById(id);
        return optionalClient.orElse(null);
    }

    @Override
    public Client updateClient(Client client) {
        if (clientRepository.existsById(client.getId())) {
            return clientRepository.save(client);
        }
        return null; // Ou vous pouvez lever une exception si le client n'existe pas
    }

    @Override
    public void deleteClient(String id) {
        clientRepository.deleteById(id);
    }
}