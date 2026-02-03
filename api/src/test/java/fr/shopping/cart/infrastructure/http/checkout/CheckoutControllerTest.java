package fr.shopping.cart.infrastructure.http.checkout;

import fr.shopping.cart.domain.cart.models.Cart;
import fr.shopping.cart.domain.checkout.services.CheckoutService;
import fr.shopping.cart.infrastructure.http.cart.mappers.CartResponseMapper;
import fr.shopping.cart.infrastructure.http.cart.mappers.impl.CartResponseMapperImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CheckoutControllerTest {
    @Mock
    private CheckoutService checkoutService;
    @Mock
    private CartResponseMapperImpl mapper;
    @InjectMocks
    private CheckoutController checkoutController;

    @BeforeEach
    void setUp() {
        when(checkoutService.checkout(anyLong())).thenReturn(new Cart(1L));
        when(mapper.toCartResponse(any())).thenCallRealMethod();
    }

    @Test
    void checkout() {
        checkoutController.checkout(1L);
        verify(checkoutService).checkout(1L);
        verify(mapper).toCartResponse(any());
    }
}