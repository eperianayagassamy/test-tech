package fr.shopping.cart.domain.cart.models;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String label;

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.PERSIST,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Offer> offers = new ArrayList<>();

    public Product(String label) {
        this.label = label;
    }

    public Optional<Offer> getOfferById(Long offerId) {
        return offers.stream()
                .filter(o -> o.getId().equals(offerId))
                .findFirst();
    }
}
