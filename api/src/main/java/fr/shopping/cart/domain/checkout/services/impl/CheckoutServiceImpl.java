package fr.shopping.cart.domain.checkout.services.impl;

import fr.shopping.cart.infrastructure.jpa.cart.CartRepository;
import fr.shopping.cart.infrastructure.jpa.cart.ProductRepository;
import fr.shopping.cart.domain.cart.exceptions.CartNotFoundException;
import fr.shopping.cart.domain.cart.exceptions.InsufficientStockException;
import fr.shopping.cart.domain.cart.exceptions.OfferNotFoundException;
import fr.shopping.cart.domain.cart.exceptions.ProductNotFoundException;
import fr.shopping.cart.domain.checkout.services.CheckoutService;
import fr.shopping.cart.infrastructure.http.cart.mappers.CartResponseMapper;
import fr.shopping.cart.domain.cart.models.Cart;
import fr.shopping.cart.domain.cart.models.CartLine;
import fr.shopping.cart.domain.cart.models.Offer;
import fr.shopping.cart.domain.cart.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartResponseMapper cartResponseMapper;

    @Override
    /**
     * Valide le panier d'un utilisateur et transforme celui-ci en commande.
     *
     * @param userId l'identifiant de l'utilisateur dont le panier doit être validé
     * @return le panier validé (avant suppression)
     * @throws CartNotFoundException si le panier de l'utilisateur n'existe pas
     * @throws ProductNotFoundException si un produit dans le panier n'existe pas
     * @throws OfferNotFoundException si une offre dans le panier n'existe pas
     * @throws InsufficientStockException si le stock disponible est insuffisant pour une ou plusieurs lignes du panier
     */
    public Cart checkout(Long userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));

        for (CartLine line : cart.getLines()) {
            final Product product = productRepository.findById(line.getProduct().getId()).orElseThrow(() -> new ProductNotFoundException(line.getProduct().getId()));
            final Offer offer = product.getOfferById(line.getOffer().getId()).orElseThrow(() -> new OfferNotFoundException(line.getOffer().getId()));
            if (!offer.hasSufficientStock(line.getQuantity())) {
                throw new InsufficientStockException(product.getId(), offer.getId());
            } else {
                offer.decreaseStock(line.getQuantity());
                productRepository.save(product);
            }
        }

        cartRepository.delete(cart);

        return cart;
    }
}

