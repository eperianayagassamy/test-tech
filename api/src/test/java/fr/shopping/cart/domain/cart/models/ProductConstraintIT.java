package fr.shopping.cart.domain.cart.models;

import fr.shopping.cart.infrastructure.jpa.cart.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
class ProductRepositoryIT {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("L'insertion doit réussir avec un label valide")
    void shouldSaveProductSuccessfully() {
        Product product = new Product("iPhone 15");

        Product savedProduct = productRepository.saveAndFlush(product);

        assertNotNull(savedProduct.getId());
    }

    @Test
    @DisplayName("L'insertion doit échouer si le label est null")
    void shouldThrowExceptionWhenLabelIsNull() {
        Product product = new Product(null);

        assertThrows(DataIntegrityViolationException.class, () -> productRepository.saveAndFlush(product));
    }

    @Test
    @DisplayName("Doit sauvegarder les offres via la cascade du produit")
    void shouldPersistOffersViaCascade() {
        Product product = new Product("MacBook Pro");

        Offer offer1 = new Offer(new BigDecimal("2000.00"), product);
        offer1.setDiscountPercent(10);
        offer1.setState(State.NEUF);
        offer1.setStockQty(5);

        Offer offer2 = new Offer(new BigDecimal("1800.00"), product);
        offer2.setDiscountPercent(0);
        offer2.setState(State.RECONDITIONNE);
        offer2.setStockQty(2);

        product.getOffers().add(offer1);
        product.getOffers().add(offer2);

        Product savedProduct = productRepository.saveAndFlush(product);

        Product foundProduct = productRepository.findById(savedProduct.getId()).orElseThrow();

        assertNotNull(foundProduct.getId());
        assertEquals(2, foundProduct.getOffers().size(), "Le produit devrait avoir 2 offres sauvegardées en cascade");

        Offer savedOffer = foundProduct.getOffers().get(0);
        assertNotNull(savedOffer.getId(), "L'offre devrait avoir un ID généré par la base");
        assertEquals(foundProduct.getId(), savedOffer.getProduct().getId(), "La clé étrangère product_id doit correspondre");
    }

    @Test
    @DisplayName("Doit supprimer l'offre en base quand elle est retirée de la liste du produit")
    void shouldDeleteOfferWhenRemovedFromList() {
        Product product = new Product("iPad");
        Offer offer = new Offer(new BigDecimal("500.00"), product);
        offer.setState(State.NEUF);
        product.getOffers().add(offer);
        productRepository.saveAndFlush(product);

        Product loadedProduct = productRepository.findById(product.getId()).orElseThrow();
        loadedProduct.getOffers().remove(0);
        productRepository.saveAndFlush(loadedProduct);

        Product finalProduct = productRepository.findById(product.getId()).orElseThrow();
        assertTrue(finalProduct.getOffers().isEmpty(), "L'offre doit avoir été supprimée de la base");
    }
}