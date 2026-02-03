package fr.shopping.cart.infrastructure.http.cart.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record UpdateItemRequestDto(
        @NotNull Long productId,
        @NotNull Long offerId,
        @Positive int quantity
) { }
