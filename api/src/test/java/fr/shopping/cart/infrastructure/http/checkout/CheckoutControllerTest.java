package fr.shopping.cart.infrastructure.http.checkout;

import fr.shopping.cart.domain.cart.models.Cart;
import fr.shopping.cart.domain.checkout.services.CheckoutService;
import fr.shopping.cart.infrastructure.http.cart.dtos.CartResponseDto;
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
class CheckoutControllerTest {

    @Mock
    private CheckoutService checkoutService;

    @Mock
    private CartResponseMapper cartResponseMapper;

    @InjectMocks
    private CheckoutController checkoutController;

    private final Long USER_ID = 1L;

    @Test
    @DisplayName("checkout doit retourner 200 et le panier finalisé mappé")
    void checkout_ShouldReturnOk() {
        Cart finalizedCart = new Cart(USER_ID);
        CartResponseDto expectedResponse = new CartResponseDto(1L, Collections.emptyList(), BigDecimal.ZERO);

        when(checkoutService.checkout(USER_ID)).thenReturn(finalizedCart);
        when(cartResponseMapper.toCartResponse(finalizedCart)).thenReturn(expectedResponse);

        ResponseEntity<CartResponseDto> response = checkoutController.checkout(USER_ID);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());

        verify(checkoutService, times(1)).checkout(USER_ID);
        verify(cartResponseMapper, times(1)).toCartResponse(finalizedCart);
    }
}