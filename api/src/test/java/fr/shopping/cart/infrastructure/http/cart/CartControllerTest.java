package fr.shopping.cart.infrastructure.http.cart;

import fr.shopping.cart.domain.cart.models.Cart;
import fr.shopping.cart.domain.cart.services.CartService;
import fr.shopping.cart.infrastructure.http.cart.dtos.AddItemRequestDto;
import fr.shopping.cart.infrastructure.http.cart.dtos.CartResponseDto;
import fr.shopping.cart.infrastructure.http.cart.dtos.UpdateItemRequestDto;
import fr.shopping.cart.infrastructure.http.cart.mappers.CartResponseMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @Mock
    private CartResponseMapper cartResponseMapper;

    @InjectMocks
    private CartController cartController;

    private final Long USER_ID = 1L;

    @Test
    @DisplayName("getCart doit retourner 200 et le DTO mappé")
    void getCart_ShouldReturnOk() {
        Cart mockCart = new Cart(USER_ID);
        CartResponseDto expectedDto = new CartResponseDto(1L, Collections.emptyList(), BigDecimal.ZERO);

        when(cartService.getCart(USER_ID)).thenReturn(mockCart);
        when(cartResponseMapper.toCartResponse(mockCart)).thenReturn(expectedDto);

        ResponseEntity<CartResponseDto> response = cartController.getCart(USER_ID);

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedDto, response.getBody());
        verify(cartService, times(1)).getCart(USER_ID);
    }

    @Test
    @DisplayName("addItem doit appeler le service et retourner 201")
    void addItem_ShouldReturnCreated() {
        AddItemRequestDto request = new AddItemRequestDto(10L, 20L);

        ResponseEntity<Void> response = cartController.addItem(USER_ID, request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(cartService).addItem(USER_ID, request);
    }

    @Test
    @DisplayName("updateItemQuantity doit retourner 204")
    void updateItemQuantity_ShouldReturnNoContent() {
        UpdateItemRequestDto request = new UpdateItemRequestDto(10L, 20L, 5);

        ResponseEntity<Void> response = cartController.updateItemQuantity(USER_ID, request);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService).updateItemQuantity(USER_ID, request);
    }

    @Test
    @DisplayName("removeItem doit retourner 204")
    void removeItem_ShouldReturnNoContent() {
        ResponseEntity<Void> response = cartController.removeItem(USER_ID, 10L, 20L);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(cartService).removeItem(USER_ID, 10L, 20L);
    }
}