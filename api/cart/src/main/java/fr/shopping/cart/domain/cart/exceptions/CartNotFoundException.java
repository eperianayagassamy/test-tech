package fr.shopping.cart.domain.cart.exceptions;

public class CartNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Cart not found for userId=%d";

    public CartNotFoundException(Long userId){
        super(String.format(MESSAGE, userId));
    }
}
