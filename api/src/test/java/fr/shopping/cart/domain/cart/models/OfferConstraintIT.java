package fr.shopping.cart.domain.cart.models;

import org.hibernate.exception.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
class OfferConstraintsIT {

    @Autowired
    private TestEntityManager entityManager;

    private Product sharedProduct;

    @BeforeEach
    void setUp() {
        sharedProduct = new Product("Test Product");
        entityManager.persistAndFlush(sharedProduct);
    }

    @Test
    @DisplayName("Doit échouer si le State est null")
    void shouldFailWhenStateIsNull() {
        Offer offer = new Offer(new BigDecimal("100"), sharedProduct);
        offer.setState(null);
        offer.setDiscountPercent(0);
        offer.setStockQty(1);


        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(offer);
        });
    }

    @ParameterizedTest
    @ValueSource(ints = {-1, 101})
    @DisplayName("Doit échouer si discountPercent est hors de [0, 100]")
    void shouldFailWhenDiscountIsOutOfRange(int invalidDiscount) {
        Offer offer = new Offer(new BigDecimal("100"), sharedProduct);
        offer.setState(State.NEUF);
        offer.setDiscountPercent(invalidDiscount);
        offer.setStockQty(1);

        // L'exception est levée car la DB refuse le CHECK (discount_percent BETWEEN 0 AND 100)
        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(offer);
        });
    }

    @Test
    @DisplayName("Doit échouer si le produit est null")
    void shouldFailWhenProductIsNull() {
        Offer offer = new Offer();
        offer.setPrice(new BigDecimal("100"));
        offer.setState(State.NEUF);
        offer.setProduct(null);
        offer.setStockQty(1);


        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(offer);
        });
    }

    @Test
    @DisplayName("L'insertion doit échouer si stockQty est négatif")
    void shouldFailWhenStockIsNegative() {
        Offer offer = new Offer(new BigDecimal("100"), sharedProduct);
        offer.setState(State.NEUF);
        offer.setStockQty(-1);

        assertThrows(ConstraintViolationException.class, () -> {
            entityManager.persistAndFlush(offer);
        });
    }
}