package com.isge.demo.restController;

import com.isge.demo.entity.Commande;
import com.isge.demo.service.CommandeService;
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
 * Contrôleur REST pour gérer les opérations CRUD des commandes avec validation et gestion des erreurs.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/commandes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Gestion des Commandes", description = "Opérations de gestion des commandes")
@Validated
public class CommandeRestController {
    private final CommandeService commandeService;

    @Autowired
    public CommandeRestController(CommandeService commandeService) {
        this.commandeService = commandeService;
    }

    /**
     * Récupère la liste de toutes les commandes.
     *
     * @return une réponse avec la liste des commandes
     */
    @Operation(
        summary = "Récupérer toutes les commandes", 
        description = "Retourne une liste complète de toutes les commandes enregistrées dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des commandes récupérée avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucune commande trouvée")
    })
    @GetMapping
    public ResponseEntity<List<Commande>> getAllCommandes() {
        List<Commande> commandesList = commandeService.allCommandes();
        if (commandesList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(commandesList);
    }

    /**
     * Récupère une commande par son identifiant.
     *
     * @param id l'identifiant de la commande
     * @return une réponse avec la commande si elle existe
     * @throws ResourceNotFoundException si la commande n'est pas trouvée
     */
    @Operation(
        summary = "Rechercher une commande", 
        description = "Retrouve une commande spécifique en utilisant son identifiant unique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande trouvée avec succès"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Commande> getCommandeById(
        @Parameter(description = "Identifiant unique de la commande", required = true) 
        @PathVariable String id
    ) {
        Commande commande = commandeService.readCommande(id);
        if (commande != null) {
            return ResponseEntity.ok(commande);
        } else {
            throw new ResourceNotFoundException("Commande", "id", id);
        }
    }

    /**
     * Crée une nouvelle commande.
     *
     * @param commande la commande à créer
     * @param bindingResult le résultat de la validation
     * @return une réponse avec la commande enregistrée
     */
    @Operation(
        summary = "Créer une nouvelle commande", 
        description = "Ajoute une nouvelle commande dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Commande créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Commande> createCommande(
        @Parameter(description = "Détails de la commande à créer", required = true) 
        @Valid @RequestBody Commande commande,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(formatValidationErrors(bindingResult));
        }
        
        commande.setId(null);
        Commande savedCommande = commandeService.createCommande(commande);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCommande);
    }

    /**
     * Met à jour une commande existante.
     *
     * @param id l'identifiant de la commande à mettre à jour
     * @param commande les nouvelles données de la commande
     * @param bindingResult le résultat de la validation
     * @return une réponse avec la commande mise à jour
     * @throws ResourceNotFoundException si la commande n'est pas trouvée
     */
    @Operation(
        summary = "Mettre à jour une commande", 
        description = "Modifie les informations d'une commande existante"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Commande mise à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Commande> updateCommande(
        @Parameter(description = "Identifiant de la commande à mettre à jour", required = true) 
        @PathVariable String id, 
        @Parameter(description = "Nouvelles informations de la commande", required = true) 
        @Valid @RequestBody Commande commande,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(formatValidationErrors(bindingResult));
        }
        
        commande.setId(id);
        Commande updatedCommande = commandeService.updateCommande(commande);
        if (updatedCommande != null) {
            return ResponseEntity.ok(updatedCommande);
        } else {
            throw new ResourceNotFoundException("Commande", "id", id);
        }
    }

    /**
     * Supprime une commande par son identifiant.
     *
     * @param id l'identifiant de la commande à supprimer
     * @return une réponse avec un statut 204 si la suppression est réussie
     * @throws ResourceNotFoundException si la commande n'est pas trouvée
     */
    @Operation(
        summary = "Supprimer une commande", 
        description = "Supprime définitivement une commande de la base de données"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Commande supprimée avec succès"),
        @ApiResponse(responseCode = "404", description = "Commande non trouvée")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCommande(
        @Parameter(description = "Identifiant de la commande à supprimer", required = true) 
        @PathVariable String id
    ) {
        Commande commande = commandeService.readCommande(id);
        if (commande != null) {
            commandeService.deleteCommande(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException("Commande", "id", id);
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