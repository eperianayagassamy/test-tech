package fr.shopping.cart.domain.cart.exceptions;

public class ProductNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Product not found for productId=%d";

    public ProductNotFoundException(Long productId){
        super(String.format(MESSAGE, productId));
    }
}
