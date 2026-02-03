package fr.shopping.cart.domain.cart.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
public class Offer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private BigDecimal price;

    @Column(
            nullable = false,
            columnDefinition = "INTEGER CHECK (discount_percent BETWEEN 0 AND 100)"
    )
    private int discountPercent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private int stockQty;

    @Column(nullable = false)
    private State state;

    public Offer(BigDecimal price, Product product) {
        this.price = price;
        this.product = product;
    }

    public BigDecimal getFinalUnitPrice() {
        final BigDecimal discount = price.multiply(BigDecimal.valueOf(discountPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return price.subtract(discount);
    }

    public void decreaseStock(int quantity) {
        this.stockQty -= quantity;
    }

    public boolean hasSufficientStock(int quantity) {
        return this.stockQty >= quantity;
    }

    public boolean hasEmptyStock() {
        return this.stockQty == 0;
    }
}
