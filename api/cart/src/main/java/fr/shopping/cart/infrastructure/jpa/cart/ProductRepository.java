package fr.shopping.cart.infrastructure.jpa.cart;

import fr.shopping.cart.domain.cart.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}

