package fr.shopping.cart.domain.cart.exceptions;

public class OfferNotFoundException extends RuntimeException {
    private static final String MESSAGE = "Offer not found for offerId=%d";

    public OfferNotFoundException(Long offerId){
        super(String.format(MESSAGE, offerId));
    }
}
