package fr.shopping.cart.domain.cart.services;

import fr.shopping.cart.infrastructure.http.cart.dtos.AddItemRequestDto;
import fr.shopping.cart.infrastructure.http.cart.dtos.UpdateItemRequestDto;
import fr.shopping.cart.domain.cart.models.Cart;

public interface CartService {

    Cart getCart(Long userId);

    void addItem(Long userId, AddItemRequestDto request);

    void removeItem(Long userId, Long productId, Long offerId);

    void updateItemQuantity(Long userId, UpdateItemRequestDto requestDto);
}

