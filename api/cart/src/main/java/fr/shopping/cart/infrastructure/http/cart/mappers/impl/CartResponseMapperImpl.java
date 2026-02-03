package fr.shopping.cart.infrastructure.http.cart.mappers.impl;

import fr.shopping.cart.infrastructure.http.cart.dtos.CartLineResponseDto;
import fr.shopping.cart.infrastructure.http.cart.dtos.CartResponseDto;
import fr.shopping.cart.infrastructure.http.cart.mappers.CartResponseMapper;
import fr.shopping.cart.domain.cart.models.Cart;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CartResponseMapperImpl implements CartResponseMapper {
    @Override
    public CartResponseDto toCartResponse(Cart cart) {
        List<CartLineResponseDto> lines = cart.getLines().stream()
                .map(line -> {
                    BigDecimal unitPrice = line.getOffer().getFinalUnitPrice();
                    BigDecimal lineTotal = unitPrice.multiply(BigDecimal.valueOf(line.getQuantity()));

                    return new CartLineResponseDto(
                            line.getProduct().getId(),
                            line.getOffer().getId(),
                            line.getOffer().getState(),
                            line.getQuantity(),
                            unitPrice,
                            lineTotal
                    );
                })
                .toList();

        BigDecimal total = lines.stream()
                .map(CartLineResponseDto::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new CartResponseDto(cart.getUserId(), lines, total);
    }
}
