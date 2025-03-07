package com.isge.demo.restController;

import com.isge.demo.entity.Plat;
import com.isge.demo.service.PlatService;
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
 * Contrôleur REST pour gérer les opérations CRUD des plats.
 * Ce contrôleur expose des points d'accès pour interagir avec les plats avec validation et gestion d'erreurs.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/plats")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Gestion des Plats", description = "Opérations de gestion des plats")
@Validated
public class PlatRestController {
    private final PlatService platService;

    @Autowired
    public PlatRestController(PlatService platService) {
        this.platService = platService;
    }

    /**
     * Récupère la liste de tous les plats.
     *
     * @return une réponse avec la liste des plats
     */
    @Operation(
        summary = "Récupérer tous les plats", 
        description = "Retourne une liste complète de tous les plats disponibles dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des plats récupérée avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucun plat trouvé")
    })
    @GetMapping
    public ResponseEntity<List<Plat>> getAllPlats() {
        List<Plat> platList = platService.allPlats();
        if (platList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(platList);
    }

    /**
     * Récupère un plat par son identifiant.
     *
     * @param id l'identifiant du plat
     * @return une réponse avec le plat s'il existe
     * @throws ResourceNotFoundException si le plat n'est pas trouvé
     */
    @Operation(
        summary = "Rechercher un plat", 
        description = "Retrouve un plat spécifique en utilisant son identifiant unique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plat trouvé avec succès"),
        @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Plat> getPlatById(
        @Parameter(description = "Identifiant unique du plat", required = true) 
        @PathVariable String id
    ) {
        Plat plat = platService.readPlat(id);
        if (plat != null) {
            return ResponseEntity.ok(plat);
        } else {
            throw new ResourceNotFoundException("Plat", "id", id);
        }
    }

    /**
     * Crée un nouveau plat.
     *
     * @param plat le plat à créer
     * @return une réponse avec le plat enregistré
     */
    @Operation(
        summary = "Créer un nouveau plat", 
        description = "Ajoute un nouveau plat dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Plat créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Plat> createPlat(
        @Parameter(description = "Détails du plat à créer", required = true) 
        @Valid @RequestBody Plat plat,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(formatValidationErrors(bindingResult));
        }
        
        plat.setId(null);  // Assurez-vous que l'ID est null pour que le système en génère un nouveau
        Plat savedPlat = platService.createPlat(plat);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPlat);
    }

    /**
     * Met à jour un plat existant.
     *
     * @param id l'identifiant du plat à mettre à jour
     * @param plat les nouvelles données du plat
     * @return une réponse avec le plat mis à jour
     * @throws ResourceNotFoundException si le plat n'est pas trouvé
     */
    @Operation(
        summary = "Mettre à jour un plat", 
        description = "Modifie les informations d'un plat existant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Plat mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Plat non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Plat> updatePlat(
        @Parameter(description = "Identifiant du plat à mettre à jour", required = true) 
        @PathVariable String id, 
        @Parameter(description = "Nouvelles informations du plat", required = true) 
        @Valid @RequestBody Plat plat,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(formatValidationErrors(bindingResult));
        }
        
        plat.setId(id);
        Plat updatedPlat = platService.updatePlat(plat);
        if (updatedPlat != null) {
            return ResponseEntity.ok(updatedPlat);
        } else {
            throw new ResourceNotFoundException("Plat", "id", id);
        }
    }

    /**
     * Supprime un plat par son identifiant.
     *
     * @param id l'identifiant du plat à supprimer
     * @return une réponse avec un statut 204 si la suppression est réussie
     * @throws ResourceNotFoundException si le plat n'est pas trouvé
     */
    @Operation(
        summary = "Supprimer un plat", 
        description = "Supprime définitivement un plat de la base de données"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Plat supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Plat non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePlat(
        @Parameter(description = "Identifiant du plat à supprimer", required = true) 
        @PathVariable String id
    ) {
        Plat plat = platService.readPlat(id);
        if (plat != null) {
            platService.deletePlat(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException("Plat", "id", id);
        }
    }

    /**
     * Récupère les plats disponibles.
     *
     * @return une réponse avec la liste des plats disponibles
     */
    @Operation(
        summary = "Récupérer les plats disponibles", 
        description = "Retourne une liste des plats marqués comme disponibles"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des plats disponibles récupérée avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucun plat disponible trouvé")
    })
    @GetMapping("/disponibles")
    public ResponseEntity<List<Plat>> getPlatsDisponibles() {
        List<Plat> platsDisponibles = platService.allPlats().stream()
            .filter(Plat::isDisponible)
            .collect(Collectors.toList());
            
        if (platsDisponibles.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(platsDisponibles);
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