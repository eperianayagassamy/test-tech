package fr.shopping.cart.domain.checkout.services;

import fr.shopping.cart.domain.cart.models.Cart;

public interface CheckoutService {
    Cart checkout(Long userId);
}
