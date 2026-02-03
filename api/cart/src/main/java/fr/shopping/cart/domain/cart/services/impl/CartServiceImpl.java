package fr.shopping.cart.domain.cart.services.impl;

import fr.shopping.cart.infrastructure.jpa.cart.CartRepository;
import fr.shopping.cart.infrastructure.jpa.cart.ProductRepository;
import fr.shopping.cart.domain.cart.services.CartService;
import fr.shopping.cart.infrastructure.http.cart.dtos.AddItemRequestDto;
import fr.shopping.cart.infrastructure.http.cart.dtos.UpdateItemRequestDto;
import fr.shopping.cart.domain.cart.exceptions.CartLineNotFoundException;
import fr.shopping.cart.domain.cart.exceptions.CartNotFoundException;
import fr.shopping.cart.domain.cart.exceptions.InsufficientStockException;
import fr.shopping.cart.domain.cart.exceptions.OfferNotFoundException;
import fr.shopping.cart.domain.cart.exceptions.ProductNotFoundException;
import fr.shopping.cart.domain.cart.models.Cart;
import fr.shopping.cart.domain.cart.models.CartLine;
import fr.shopping.cart.domain.cart.models.Offer;
import fr.shopping.cart.domain.cart.models.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    /** Wrapper interne pour coupler un produit et son offre. */
    private record ProductAndOffer(Product product, Offer offer) {}

    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    /**
     * Récupère le panier d'un utilisateur. Si le panier n'existe pas, il est créé automatiquement.
     *
     * @param userId identifiant de l'utilisateur
     * @return {@link Cart} contenant les lignes du panier et le total
     */
    @Override
    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> createCart(userId));
    }

    /**
     * Ajoute un article au panier. Si la ligne existe déjà, la quantité est incrémentée.
     *
     * @param userId  identifiant de l'utilisateur
     * @param request {@link AddItemRequestDto} contenant productId et offerId
     * @throws InsufficientStockException si le stock est insufisant
     */
    @Override
    public void addItem(Long userId, AddItemRequestDto request) {
        final Long productId = request.productId();
        final Long offerId = request.offerId();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseGet(() -> cartRepository.save(new Cart(userId)));

        final ProductAndOffer po = loadProductAndOffer(productId, offerId);

        cart.findLine(productId, offerId).ifPresentOrElse(
                line -> {
                    int newQuantity = line.getQuantity() + 1;

                    if (!po.offer().hasSufficientStock(newQuantity)) {
                        throw new InsufficientStockException(productId, offerId);
                    }
                    line.updateQuantity(newQuantity);
                },
                () -> {
                    if (po.offer().hasEmptyStock()) {
                        throw new InsufficientStockException(productId, offerId);
                    }

                    CartLine newLine = new CartLine(cart, po.product(), po.offer(), 1);
                    cart.getLines().add(newLine);
                }
        );
        cartRepository.save(cart);
    }


    /**
     * Met à jour la quantité d'un article dans le panier.
     * @param userId  identifiant de l'utilisateur
     * @param request {@link UpdateItemRequestDto} contenant productId, offerId et la nouvelle quantité
     * @throws CartNotFoundException si le panier n'existe pas
     * @throws CartLineNotFoundException si la ligne n'existe pas dans le panier
     * @throws InsufficientStockException si le stock est insufisant
     */
    @Override
    public void updateItemQuantity(Long userId, UpdateItemRequestDto request) {
        final Long productId = request.productId();
        final Long offerId = request.offerId();
        final int quantity = request.quantity();

        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        final ProductAndOffer po = loadProductAndOffer(productId, offerId);

        cart.findLine(productId, offerId).orElseThrow(() -> new CartLineNotFoundException(productId, offerId));

        if(!po.offer().hasSufficientStock(quantity)){
            throw new InsufficientStockException(productId, offerId);
        }

        cart.updateItemQuantity(
                po.product(),
                po.offer(),
                quantity
        );
        cartRepository.save(cart);

    }

    /**
     * Supprime une ligne du panier.
     * @param userId    identifiant de l'utilisateur
     * @param productId identifiant du produit
     * @param offerId   identifiant de l'offre
     * @throws CartNotFoundException si le panier n'existe pas
     */
    @Override
    public void removeItem(Long userId, Long productId, Long offerId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new CartNotFoundException(userId));
        cart.getLines().removeIf(line ->
                line.getProduct().getId().equals(productId) &&
                        line.getOffer().getId().equals(offerId)
        );
        cartRepository.save(cart);
    }

    /**
     * Charge un produit et son offre. Lève une exception si l'un ou l'autre n'existe pas.
     * @param productId identifiant du produit
     * @param offerId   identifiant de l'offre
     * @return {@link ProductAndOffer} couplant le produit et l'offre
     * @throws ProductNotFoundException si le produit n'existe pas
     * @throws OfferNotFoundException si l'offre n'existe pas
     */
    private ProductAndOffer loadProductAndOffer(Long productId, Long offerId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        Offer offer = product.getOfferById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));

        return new ProductAndOffer(product, offer);
    }

    /**
     * Crée un panier vide pour un utilisateur et le sauvegarde en mémoire.
     *
     * @param userId identifiant de l'utilisateur
     * @return panier créé
     */
    private Cart createCart(Long userId){
        return cartRepository.save(new Cart(userId));
    }

}

