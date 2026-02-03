package fr.shopping.cart.domain.cart.services.impl;

import fr.shopping.cart.domain.cart.exceptions.CartLineNotFoundException;
import fr.shopping.cart.domain.cart.exceptions.InsufficientStockException;
import fr.shopping.cart.domain.cart.exceptions.ProductNotFoundException;
import fr.shopping.cart.domain.cart.models.Cart;
import fr.shopping.cart.domain.cart.models.CartLine;
import fr.shopping.cart.domain.cart.models.Offer;
import fr.shopping.cart.domain.cart.models.Product;
import fr.shopping.cart.infrastructure.http.cart.dtos.AddItemRequestDto;
import fr.shopping.cart.infrastructure.http.cart.dtos.UpdateItemRequestDto;
import fr.shopping.cart.infrastructure.jpa.cart.CartRepository;
import fr.shopping.cart.infrastructure.jpa.cart.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    private final Long USER_ID = 1L;
    private final Long PRODUCT_ID = 10L;
    private final Long OFFER_ID = 100L;

    @Nested
    @DisplayName("Tests pour addItem")
    class AddItemTests {

        @Test
        @DisplayName("Doit ajouter une nouvelle ligne si l'article n'est pas présent")
        void shouldAddNewLine() {
            Cart cart = new Cart(USER_ID);
            Product product = new Product("iPhone");
            product.setId(PRODUCT_ID);
            Offer offer = new Offer(new BigDecimal("999"), product);
            offer.setId(OFFER_ID);
            offer.setStockQty(10);
            product.getOffers().add(offer);

            AddItemRequestDto request = new AddItemRequestDto(PRODUCT_ID, OFFER_ID);

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

            cartService.addItem(USER_ID, request);

            assertEquals(1, cart.getLines().size());
            assertEquals(1, cart.getLines().get(0).getQuantity());
            verify(cartRepository).save(cart);
        }

        @Test
        @DisplayName("Doit lever une exception si le stock est vide pour un nouvel ajout")
        void shouldThrowExceptionWhenStockEmpty() {
            Cart cart = new Cart(USER_ID);
            Product product = new Product("iPhone");
            product.setId(PRODUCT_ID);
            Offer offer = new Offer(new BigDecimal("999"), product);
            offer.setId(OFFER_ID);
            offer.setStockQty(0);
            product.getOffers().add(offer);

            AddItemRequestDto request = new AddItemRequestDto(PRODUCT_ID, OFFER_ID);

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

            assertThrows(InsufficientStockException.class, () -> cartService.addItem(USER_ID, request));
            verify(cartRepository, never()).save(any(Cart.class));
        }
    }

    @Nested
    @DisplayName("Tests pour loadProductAndOffer")
    class InternalLoadingTests {

        @Test
        @DisplayName("Doit lever ProductNotFoundException si le produit n'existe pas")
        void shouldThrowProductNotFound() {
            AddItemRequestDto request = new AddItemRequestDto(PRODUCT_ID, OFFER_ID);
            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(new Cart(USER_ID)));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.empty());

            assertThrows(ProductNotFoundException.class, () -> cartService.addItem(USER_ID, request));
        }
    }

    @Nested
    @DisplayName("Tests pour updateItemQuantity")
    class UpdateItemQuantityTests {

        @Test
        @DisplayName("Doit mettre à jour la quantité avec succès")
        void shouldUpdateQuantitySuccessfully() {
            // Given
            Product product = new Product("iPhone");
            product.setId(PRODUCT_ID);
            Offer offer = new Offer(new BigDecimal("999"), product);
            offer.setId(OFFER_ID);
            offer.setStockQty(10);
            product.getOffers().add(offer);

            Cart cart = new Cart(USER_ID);
            CartLine line = new CartLine(cart, product, offer, 1);
            cart.getLines().add(line);

            UpdateItemRequestDto request = new UpdateItemRequestDto(PRODUCT_ID, OFFER_ID, 5);

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

            cartService.updateItemQuantity(USER_ID, request);

            assertEquals(5, line.getQuantity());
            verify(cartRepository).save(cart);
        }

        @Test
        @DisplayName("Doit lever une exception si le stock est insuffisant lors de l'update")
        void shouldThrowExceptionWhenStockInsufficient() {
            Product product = new Product("iPhone");
            product.setId(PRODUCT_ID);
            Offer offer = new Offer(new BigDecimal("999"), product);
            offer.setId(OFFER_ID);
            offer.setStockQty(3);
            product.getOffers().add(offer);

            Cart cart = new Cart(USER_ID);
            CartLine line = new CartLine(cart, product, offer, 1);
            cart.getLines().add(line);

            UpdateItemRequestDto request = new UpdateItemRequestDto(PRODUCT_ID, OFFER_ID, 5);

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

            assertThrows(InsufficientStockException.class,
                    () -> cartService.updateItemQuantity(USER_ID, request));

            assertEquals(1, line.getQuantity());
            verify(cartRepository, never()).save(any(Cart.class));
        }

        @Test
        @DisplayName("Doit lever CartLineNotFoundException si la ligne n'est pas dans le panier")
        void shouldThrowExceptionWhenLineNotFound() {
            Product product = new Product("iPhone");
            product.setId(PRODUCT_ID);
            Offer offer = new Offer(new BigDecimal("999"), product);
            offer.setId(OFFER_ID);
            product.getOffers().add(offer);

            Cart cart = new Cart(USER_ID);
            UpdateItemRequestDto request = new UpdateItemRequestDto(PRODUCT_ID, OFFER_ID, 5);

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));
            when(productRepository.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

            assertThrows(CartLineNotFoundException.class,
                    () -> cartService.updateItemQuantity(USER_ID, request));
        }
    }

    @Nested
    @DisplayName("Tests pour removeItem")
    class RemoveItemTests {

        @Test
        @DisplayName("Doit supprimer la ligne correspondante du panier")
        void shouldRemoveLineFromCart() {
            Cart cart = new Cart(USER_ID);

            Product product = new Product("iPhone");
            product.setId(PRODUCT_ID);
            Offer offer = new Offer(new BigDecimal("999"), product);
            offer.setId(OFFER_ID);

            CartLine lineToRemove = new CartLine(cart, product, offer, 1);
            cart.getLines().add(lineToRemove);

            Product otherProduct = new Product("Samsung");
            otherProduct.setId(99L);
            Offer otherOffer = new Offer(new BigDecimal("888"), otherProduct);
            otherOffer.setId(999L);
            cart.getLines().add(new CartLine(cart, otherProduct, otherOffer, 2));

            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));

            cartService.removeItem(USER_ID, PRODUCT_ID, OFFER_ID);

            assertEquals(1, cart.getLines().size(), "Il ne devrait rester qu'une seule ligne");
            assertTrue(cart.getLines().stream().noneMatch(l ->
                    l.getProduct().getId().equals(PRODUCT_ID) && l.getOffer().getId().equals(OFFER_ID)
            ), "La ligne cible doit avoir été supprimée");

            verify(cartRepository).save(cart);
        }

        @Test
        @DisplayName("Ne doit rien supprimer si la ligne n'existe pas mais que le panier existe")
        void shouldDoNothingIfLineDoesNotExist() {
            Cart cart = new Cart(USER_ID);
            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.of(cart));

            cartService.removeItem(USER_ID, PRODUCT_ID, OFFER_ID);

            assertEquals(0, cart.getLines().size());
            verify(cartRepository).save(cart);
        }

        @Test
        @DisplayName("Doit lever CartNotFoundException si le panier n'existe pas")
        void shouldThrowExceptionWhenCartNotFound() {
            when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());

            assertThrows(fr.shopping.cart.domain.cart.exceptions.CartNotFoundException.class,
                    () -> cartService.removeItem(USER_ID, PRODUCT_ID, OFFER_ID));

            verify(cartRepository, never()).save(any(Cart.class));
        }
    }

    @Test
    @DisplayName("getCart doit créer un panier s'il n'existe pas")
    void shouldCreateCartWhenNotFound() {
        when(cartRepository.findByUserId(USER_ID)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenAnswer(i -> i.getArguments()[0]);

        Cart result = cartService.getCart(USER_ID);

        assertNotNull(result);
        assertEquals(USER_ID, result.getUserId());
        verify(cartRepository, times(1)).save(any(Cart.class));
    }
}