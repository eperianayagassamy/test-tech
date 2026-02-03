package fr.shopping.cart.infrastructure.http.cart.dtos;

import fr.shopping.cart.domain.cart.models.State;

import java.math.BigDecimal;

public record CartLineResponseDto(
        Long productId,
        Long offerId,
        State state,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal lineTotal
) {}
