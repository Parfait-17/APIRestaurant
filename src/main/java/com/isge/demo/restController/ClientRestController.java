package com.isge.demo.restController;

import com.isge.demo.entity.Client;
import com.isge.demo.service.ClientService;
import com.isge.demo.exception.ResourceNotFoundException;
import com.isge.demo.exception.ErrorResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Contrôleur REST pour gérer les opérations CRUD des clients.
 * Ce contrôleur expose des points d'accès pour interagir avec les clients avec validation et gestion d'erreurs.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Gestion des Clients", description = "Opérations de gestion des clients")
@Validated
public class ClientRestController {
    private final ClientService clientService;

    @Autowired
    public ClientRestController(ClientService clientService) {
        this.clientService = clientService;
    }

    /**
     * Récupère la liste de tous les clients.
     *
     * @return une réponse avec la liste des clients
     */
    @Operation(
        summary = "Récupérer tous les clients", 
        description = "Retourne une liste complète de tous les clients enregistrés dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des clients récupérée avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucun client trouvé")
    })
    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        List<Client> clientsList = clientService.allClients();
        if (clientsList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(clientsList);
    }

    /**
     * Récupère un client par son identifiant.
     *
     * @param id l'identifiant du client
     * @return une réponse avec le client s'il existe
     * @throws ResourceNotFoundException si le client n'est pas trouvé
     */
    @Operation(
        summary = "Rechercher un client", 
        description = "Retrouve un client spécifique en utilisant son identifiant unique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client trouvé avec succès"),
        @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Client> getClientById(
        @Parameter(description = "Identifiant unique du client", required = true) 
        @PathVariable String id
    ) {
        Client client = clientService.readClient(id);
        if (client != null) {
            return ResponseEntity.ok(client);
        } else {
            throw new ResourceNotFoundException("Client", "id", id);
        }
    }

    /**
     * Crée un nouveau client.
     *
     * @param client le client à créer
     * @param bindingResult le résultat de la validation
     * @return une réponse avec le client enregistré
     */
    @Operation(
        summary = "Créer un nouveau client", 
        description = "Ajoute un nouveau client dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Client créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Client> createClient(
        @Parameter(description = "Détails du client à créer", required = true) 
        @Valid @RequestBody Client client,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(formatValidationErrors(bindingResult));
        }
        
        client.setId(null);
        Client savedClient = clientService.createClient(client);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
    }

    /**
     * Met à jour un client existant.
     *
     * @param id l'identifiant du client à mettre à jour
     * @param client les nouvelles données du client
     * @param bindingResult le résultat de la validation
     * @return une réponse avec le client mis à jour
     * @throws ResourceNotFoundException si le client n'est pas trouvé
     */
    @Operation(
        summary = "Mettre à jour un client", 
        description = "Modifie les informations d'un client existant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Client mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Client non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Client> updateClient(
        @Parameter(description = "Identifiant du client à mettre à jour", required = true) 
        @PathVariable String id, 
        @Parameter(description = "Nouvelles informations du client", required = true) 
        @Valid @RequestBody Client client,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(formatValidationErrors(bindingResult));
        }
        
        client.setId(id);
        Client updatedClient = clientService.updateClient(client);
        if (updatedClient != null) {
            return ResponseEntity.ok(updatedClient);
        } else {
            throw new ResourceNotFoundException("Client", "id", id);
        }
    }

    /**
     * Supprime un client par son identifiant.
     *
     * @param id l'identifiant du client à supprimer
     * @return une réponse avec un statut 204 si la suppression est réussie
     * @throws ResourceNotFoundException si le client n'est pas trouvé
     */
    @Operation(
        summary = "Supprimer un client", 
        description = "Supprime définitivement un client de la base de données"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Client supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Client non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClient(
        @Parameter(description = "Identifiant du client à supprimer", required = true) 
        @PathVariable String id
    ) {
        Client client = clientService.readClient(id);
        if (client != null) {
            clientService.deleteClient(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException("Client", "id", id);
        }
    }

    /**
     * Gère les exceptions de validation des arguments de méthode.
     * 
     * @param ex L'exception lancée
     * @return Une réponse contenant les détails des erreurs de validation
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Erreur de validation",
            errors
        );
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Gère les exceptions de ressource non trouvée.
     * 
     * @param ex L'exception lancée
     * @return Une réponse contenant les détails de l'erreur
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            null
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Gère les exceptions générales.
     * 
     * @param ex L'exception lancée
     * @return Une réponse contenant les détails de l'erreur
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Une erreur interne s'est produite",
            Map.of("error", ex.getMessage())
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    /**
     * Formate les erreurs de validation en une chaîne de caractères.
     * 
     * @param bindingResult Le résultat de la validation
     * @return Une chaîne de caractères contenant les erreurs de validation
     */
    private String formatValidationErrors(BindingResult bindingResult) {
        return bindingResult.getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
    }
}