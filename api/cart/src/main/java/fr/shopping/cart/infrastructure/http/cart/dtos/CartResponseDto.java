package fr.shopping.cart.infrastructure.http.cart.dtos;

import java.math.BigDecimal;
import java.util.List;

public record CartResponseDto(
        Long userId,
        List<CartLineResponseDto> lines,
        BigDecimal totalPrice
) {}