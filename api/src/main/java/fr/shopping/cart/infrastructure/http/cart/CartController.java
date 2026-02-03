package fr.shopping.cart.infrastructure.http.cart;

import fr.shopping.cart.domain.cart.services.CartService;
import fr.shopping.cart.infrastructure.http.cart.dtos.AddItemRequestDto;
import fr.shopping.cart.infrastructure.http.cart.dtos.CartResponseDto;
import fr.shopping.cart.infrastructure.http.cart.dtos.UpdateItemRequestDto;
import fr.shopping.cart.infrastructure.http.cart.mappers.CartResponseMapper;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Contrôleur REST pour gérer le panier d'un utilisateur.
 * Fournit des points de terminaison pour récupérer le panier, ajouter des articles, mettre à jour les quantités d'articles
 * et supprimer des articles du panier d'un utilisateur.
 * URL de base : {@code /api/v1/users/{userId}/cart} (configurable via {@code api.prefix.route})
 */
@RestController
@RequestMapping("${api.prefix.route}/users/{userId}/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;
    private final CartResponseMapper cartResponseMapper;

    /**
     * Récupère l'état actuel du panier d'un utilisateur spécifique.
     *
     * @param userId l'ID de l'utilisateur dont le panier doit être récupéré
     * @return une ResponseEntity contenant le {@link CartResponseDto} avec
     *         les lignes du panier et le prix total
     */
    @GetMapping
    public ResponseEntity<CartResponseDto> getCart(@PathVariable Long userId) {
        final CartResponseDto responseDto = cartResponseMapper.toCartResponse(cartService.getCart(userId));
        return ResponseEntity.ok(responseDto);
    }

    /**
     * Ajoute un nouvel article au panier de l'utilisateur.
     * Si l'article existe déjà dans le panier, sa quantité sera augmentée de 1.
     *
     * @param userId  l'ID de l'utilisateur
     * @param request le DTO contenant le produit et l'offre à ajouter
     * @return une ResponseEntity avec le statut HTTP 201 (Créé)
     */
    @PostMapping("/items")
    public ResponseEntity<Void> addItem(
            @PathVariable Long userId,
            @RequestBody @Valid AddItemRequestDto request
    ) {
        cartService.addItem(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * Met à jour la quantité d'un article existant dans le panier de l'utilisateur.
     *
     * @param userId  l'ID de l'utilisateur
     * @param request le DTO contenant le produit, l'offre et la nouvelle quantité
     * @return une ResponseEntity avec le statut HTTP 204 (No Content)
     */
    @PutMapping("/items")
    public ResponseEntity<Void> updateItemQuantity(
            @PathVariable Long userId,
            @RequestBody @Valid UpdateItemRequestDto request
    ) {
        cartService.updateItemQuantity(userId, request);
        return ResponseEntity.noContent().build();
    }

    /**
     * Supprime un article du panier de l'utilisateur.
     *
     * @param userId    l'ID de l'utilisateur
     * @param productId l'ID du produit à supprimer
     * @param offerId   l'ID de l'offre à supprimer
     * @return une ResponseEntity avec le statut HTTP 204 (No Content)
     */
    @DeleteMapping("/items/{productId}/{offerId}")
    public ResponseEntity<Void> removeItem(
            @PathVariable Long userId,
            @PathVariable Long productId,
            @PathVariable Long offerId
    ) {
        cartService.removeItem(userId, productId, offerId);
        return ResponseEntity.noContent().build();
    }
}
