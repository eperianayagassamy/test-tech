package fr.shopping.cart.domain.cart.exceptions;

public class InsufficientStockException extends RuntimeException {
    private static final String MESSAGE = "Insufficient stock for productId=%d, offerId=%d";

    public InsufficientStockException(Long productId, Long offerId){
        super(String.format(MESSAGE, productId, offerId));
    }
}
