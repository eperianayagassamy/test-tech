package fr.shopping.cart.domain.checkout.services.impl;

import fr.shopping.cart.domain.cart.exceptions.CartNotFoundException;
import fr.shopping.cart.domain.cart.exceptions.InsufficientStockException;
import fr.shopping.cart.domain.cart.models.Cart;
import fr.shopping.cart.domain.cart.models.CartLine;
import fr.shopping.cart.domain.cart.models.Offer;
import fr.shopping.cart.domain.cart.models.Product;
import fr.shopping.cart.infrastructure.jpa.cart.CartRepository;
import fr.shopping.cart.infrastructure.jpa.cart.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CheckoutServiceImpl checkoutService;

    private final Long USER_ID = 1L;

    @Test
    @DisplayName("checkout doit décrémenter le stock et supprimer le panier en cas de succès")
    void checkout_ShouldSucceed() {
        Product product = new Product("Laptop");
        product.setId(10L);
        Offer offer = new Offer(new BigDecimal("1000"), product);
        offer.setId(100L);
        offer.setStockQty(10);
        product.getOffers().add(offer);

        Cart cart = new Cart(USER_ID);
        CartLine line = new CartLine(cart, product, offer, 2);
        cart.getLines().add(line);

        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(productRepository.findById(10L)).thenReturn(Optional.of(product));

        Cart result = checkoutService.checkout(USER_ID);

        assertEquals(8, offer.getStockQty(), "Le stock aurait dû passer de 10 à 8");
        verify(productRepository).save(product);
        verify(cartRepository).delete(cart);
        assertEquals(USER_ID, result.getUserId());
    }

    @Test
    @DisplayName("checkout doit lever InsufficientStockException si le stock est trop bas")
    void checkout_ShouldThrowInsufficientStock() {
        Product product = new Product("Phone");
        product.setId(20L);
        Offer offer = new Offer(new BigDecimal("500"), product);
        offer.setId(200L);
        offer.setStockQty(1);
        product.getOffers().add(offer);

        Cart cart = new Cart(USER_ID);
        cart.getLines().add(new CartLine(cart, product, offer, 5));

        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
        when(productRepository.findById(20L)).thenReturn(Optional.of(product));

        assertThrows(InsufficientStockException.class, () -> checkoutService.checkout(USER_ID));

        verify(productRepository, never()).save(any());
        verify(cartRepository, never()).delete(any());
    }

    @Test
    @DisplayName("checkout doit lever CartNotFoundException si le panier n'existe pas")
    void checkout_ShouldThrowCartNotFound() {
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

        assertThrows(CartNotFoundException.class, () -> checkoutService.checkout(USER_ID));
    }
}