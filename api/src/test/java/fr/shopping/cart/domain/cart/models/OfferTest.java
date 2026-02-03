package fr.shopping.cart.domain.cart.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OfferTest {

    @Test
    @DisplayName("Prix final sans remise (0%)")
    void shouldReturnInitialPriceWhenNoDiscount() {
        Offer offer = new Offer();
        offer.setPrice(new BigDecimal("100.00"));
        offer.setDiscountPercent(0);

        BigDecimal finalPrice = offer.getFinalUnitPrice();

        assertEquals(new BigDecimal("100.00"), finalPrice);
    }

    @Test
    @DisplayName("Prix final avec remise totale (100%)")
    void shouldReturnZeroWhenFullDiscount() {
        Offer offer = new Offer();
        offer.setPrice(new BigDecimal("100.00"));
        offer.setDiscountPercent(100);

        BigDecimal finalPrice = offer.getFinalUnitPrice();


        assertEquals(0, finalPrice.compareTo(BigDecimal.ZERO));
    }

    @ParameterizedTest(name = "Prix {0} avec {1}% de remise devrait donner {2}")
    @CsvSource({
            "100.00, 20, 80.00",
            "99.99,  10, 89.99",
            "10.00,  15, 8.50",
            "49.50,  50, 24.75"
    })
    @DisplayName("Calculs de prix avec remises variées")
    void shouldCalculateCorrectFinalPrice(String price, int discount, String expected) {
        // Given
        Offer offer = new Offer();
        offer.setPrice(new BigDecimal(price));
        offer.setDiscountPercent(discount);

        // When
        BigDecimal finalPrice = offer.getFinalUnitPrice();

        // Then
        assertEquals(new BigDecimal(expected), finalPrice);
    }

    @Test
    @DisplayName("Vérification de l'arrondi HALF_UP (ex: 1/3 de remise)")
    void shouldHandleRoundingCorrectly() {
        // Given: 10€ avec 33% de remise -> 3.30 de remise -> 6.70
        Offer offer = new Offer();
        offer.setPrice(new BigDecimal("10.00"));
        offer.setDiscountPercent(33);

        // When
        BigDecimal finalPrice = offer.getFinalUnitPrice();

        // Then
        assertEquals(new BigDecimal("6.70"), finalPrice);
    }
}