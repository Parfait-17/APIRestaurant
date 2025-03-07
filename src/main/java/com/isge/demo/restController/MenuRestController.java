package com.isge.demo.restController;

import com.isge.demo.entity.Menu;
import com.isge.demo.service.MenuService;
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
 * Contrôleur REST pour gérer les opérations CRUD des menus avec validation et gestion des erreurs.
 *
 * @version 1.0
 */
@RestController
@RequestMapping("/api/menus")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Tag(name = "Gestion des Menus", description = "Opérations de gestion des menus")
@Validated
public class MenuRestController {
    private final MenuService menuService;

    @Autowired
    public MenuRestController(MenuService menuService) {
        this.menuService = menuService;
    }

    /**
     * Récupère la liste de tous les menus.
     *
     * @return une réponse avec la liste des menus
     */
    @Operation(
        summary = "Récupérer tous les menus", 
        description = "Retourne une liste complète de tous les menus disponibles dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Liste des menus récupérée avec succès"),
        @ApiResponse(responseCode = "204", description = "Aucun menu trouvé")
    })
    @GetMapping
    public ResponseEntity<List<Menu>> getAllMenus() {
        List<Menu> menusList = menuService.allMenus();
        if (menusList.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(menusList);
    }

    /**
     * Récupère un menu par son identifiant.
     *
     * @param id l'identifiant du menu
     * @return une réponse avec le menu s'il existe
     * @throws ResourceNotFoundException si le menu n'est pas trouvé
     */
    @Operation(
        summary = "Rechercher un menu", 
        description = "Retrouve un menu spécifique en utilisant son identifiant unique"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu trouvé avec succès"),
        @ApiResponse(responseCode = "404", description = "Menu non trouvé")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Menu> getMenuById(
        @Parameter(description = "Identifiant unique du menu", required = true) 
        @PathVariable String id
    ) {
        Menu menu = menuService.readMenu(id);
        if (menu != null) {
            return ResponseEntity.ok(menu);
        } else {
            throw new ResourceNotFoundException("Menu", "id", id);
        }
    }

    /**
     * Crée un nouveau menu.
     *
     * @param menu le menu à créer
     * @param bindingResult le résultat de la validation
     * @return une réponse avec le menu enregistré
     */
    @Operation(
        summary = "Créer un nouveau menu", 
        description = "Ajoute un nouveau menu dans le système"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Menu créé avec succès"),
        @ApiResponse(responseCode = "400", description = "Requête invalide")
    })
    @PostMapping
    public ResponseEntity<Menu> createMenu(
        @Parameter(description = "Détails du menu à créer", required = true) 
        @Valid @RequestBody Menu menu,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(formatValidationErrors(bindingResult));
        }
        
        menu.setId(null);
        Menu savedMenu = menuService.createMenu(menu);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedMenu);
    }

    /**
     * Met à jour un menu existant.
     *
     * @param id l'identifiant du menu à mettre à jour
     * @param menu les nouvelles données du menu
     * @param bindingResult le résultat de la validation
     * @return une réponse avec le menu mis à jour
     * @throws ResourceNotFoundException si le menu n'est pas trouvé
     */
    @Operation(
        summary = "Mettre à jour un menu", 
        description = "Modifie les informations d'un menu existant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu mis à jour avec succès"),
        @ApiResponse(responseCode = "404", description = "Menu non trouvé"),
        @ApiResponse(responseCode = "400", description = "Données invalides")
    })
    @PutMapping("/{id}")
    public ResponseEntity<Menu> updateMenu(
        @Parameter(description = "Identifiant du menu à mettre à jour", required = true) 
        @PathVariable String id, 
        @Parameter(description = "Nouvelles informations du menu", required = true) 
        @Valid @RequestBody Menu menu,
        BindingResult bindingResult
    ) {
        if (bindingResult.hasErrors()) {
            throw new IllegalArgumentException(formatValidationErrors(bindingResult));
        }
        
        menu.setId(id);
        Menu updatedMenu = menuService.updateMenu(menu);
        if (updatedMenu != null) {
            return ResponseEntity.ok(updatedMenu);
        } else {
            throw new ResourceNotFoundException("Menu", "id", id);
        }
    }

    /**
     * Supprime un menu par son identifiant.
     *
     * @param id l'identifiant du menu à supprimer
     * @return une réponse avec un statut 204 si la suppression est réussie
     * @throws ResourceNotFoundException si le menu n'est pas trouvé
     */
    @Operation(
        summary = "Supprimer un menu", 
        description = "Supprime définitivement un menu de la base de données"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Menu supprimé avec succès"),
        @ApiResponse(responseCode = "404", description = "Menu non trouvé")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMenu(
        @Parameter(description = "Identifiant du menu à supprimer", required = true) 
        @PathVariable String id
    ) {
        Menu menu = menuService.readMenu(id);
        if (menu != null) {
            menuService.deleteMenu(id);
            return ResponseEntity.noContent().build();
        } else {
            throw new ResourceNotFoundException("Menu", "id", id);
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