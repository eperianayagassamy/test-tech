package fr.shopping.cart.infrastructure.http.cart.mappers;

import fr.shopping.cart.infrastructure.http.cart.dtos.CartResponseDto;
import fr.shopping.cart.domain.cart.models.Cart;

public interface CartResponseMapper {
    CartResponseDto toCartResponse(Cart cart);
}
