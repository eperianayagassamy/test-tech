package fr.shopping.cart.config;

import fr.shopping.cart.infrastructure.jpa.cart.ProductRepository;
import fr.shopping.cart.domain.cart.models.Offer;
import fr.shopping.cart.domain.cart.models.Product;
import fr.shopping.cart.domain.cart.models.State;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initProducts(ProductRepository productRepository) {
        return args -> {

            Product iphone = new Product("iPhone 14");

            Offer iphoneNeuf = new Offer();
            iphoneNeuf.setProduct(iphone);
            iphoneNeuf.setState(State.NEUF);
            iphoneNeuf.setPrice(new BigDecimal("999.00"));
            iphoneNeuf.setStockQty(5);
            iphoneNeuf.setDiscountPercent(0);

            Offer iphoneReconditionne = new Offer();
            iphoneReconditionne.setProduct(iphone);
            iphoneReconditionne.setState(State.NEUF);
            iphoneReconditionne.setPrice(new BigDecimal("799.00"));
            iphoneReconditionne.setStockQty(5);
            iphoneReconditionne.setDiscountPercent(20);

            iphone.getOffers().add(iphoneNeuf);
            iphone.getOffers().add(iphoneReconditionne);

            productRepository.save(iphone);
        };
    }
}
