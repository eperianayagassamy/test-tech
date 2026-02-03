package fr.shopping.cart.infrastructure.http.checkout;

import fr.shopping.cart.domain.checkout.services.CheckoutService;
import fr.shopping.cart.infrastructure.http.cart.dtos.CartResponseDto;
import fr.shopping.cart.domain.cart.exceptions.CartNotFoundException;
import fr.shopping.cart.domain.cart.exceptions.InsufficientStockException;
import fr.shopping.cart.domain.cart.exceptions.OfferNotFoundException;
import fr.shopping.cart.domain.cart.exceptions.ProductNotFoundException;
import fr.shopping.cart.infrastructure.http.cart.mappers.CartResponseMapper;
import fr.shopping.cart.domain.cart.models.Cart;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling checkout operations for a user's cart.
 * Provides an endpoint to finalize the user's cart into an order.
 * During checkout, the stock of each offer is decremented and the cart is cleared.
 * Base URL: {@code /api/v1/users/{userId}/checkout} (configurable via {@code api.prefix.route})
 */
@RestController
@RequestMapping("${api.prefix.route}/users/{userId}/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;
    private final CartResponseMapper cartResponseMapper;

    /**
     * Performs checkout for the specified user's cart.
     * @param userId the ID of the user performing checkout
     * @return a ResponseEntity containing the {@link CartResponseDto} representing the finalized order
     * @throws CartNotFoundException if the user's cart does not exist
     * @throws ProductNotFoundException if a product in the cart does not exist
     * @throws OfferNotFoundException if an offer in the cart does not exist
     * @throws InsufficientStockException if any offer does not have sufficient stock
     */
    @PostMapping
    public ResponseEntity<CartResponseDto> checkout(@PathVariable Long userId) {
        Cart order = checkoutService.checkout(userId);

        return ResponseEntity.ok(cartResponseMapper.toCartResponse(order));
    }
}
