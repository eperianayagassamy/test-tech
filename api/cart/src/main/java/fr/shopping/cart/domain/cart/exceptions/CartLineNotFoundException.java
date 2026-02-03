package fr.shopping.cart.domain.cart.exceptions;

public class CartLineNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Cart has no line for productId=%d and offerId=%d";

    public CartLineNotFoundException(Long productId, Long offerId) {
        super(String.format(MESSAGE, productId, offerId));
    }
}
