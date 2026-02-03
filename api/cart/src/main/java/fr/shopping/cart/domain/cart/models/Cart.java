package fr.shopping.cart.domain.cart.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "carts")
@Getter
@Setter
@NoArgsConstructor
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @OneToMany(
            mappedBy = "cart",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<CartLine> lines = new ArrayList<>();

    public Cart(Long userId) {
        this.userId = userId;
    }

    public Optional<CartLine> findLine(Long productId, Long offerId) {
        return lines.stream()
                .filter(l ->
                        l.getProduct().getId().equals(productId) &&
                                l.getOffer().getId().equals(offerId))
                .findFirst();
    }

    public void updateItemQuantity(Product product, Offer offer, int quantity) {
        findLine(product.getId(), offer.getId())
                .ifPresent(
                        line -> line.updateQuantity(quantity)
                );
    }
}
