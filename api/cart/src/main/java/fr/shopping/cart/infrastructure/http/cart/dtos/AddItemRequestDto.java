package fr.shopping.cart.infrastructure.http.cart.dtos;

import jakarta.validation.constraints.NotNull;

public record AddItemRequestDto(
        @NotNull Long productId,
        @NotNull Long offerId
) { }
