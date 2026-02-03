package fr.shopping.cart.infrastructure.http.cart;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class CartControllerIT {

    @Autowired
    private MockMvc mockMvc;

    private final Long userId = 1L;
    private final Long productId = 1L;  // iPhone 14 neuf
    private final Long offerId = 1L;    // iPhone 14 NEUF, stock 5

    @Test
    void addItem_ShouldReturn201() throws Exception {
        String json = String.format("{\"productId\":%d,\"offerId\":%d}", productId, offerId);

        mockMvc.perform(post("/api/v1/users/{userId}/cart/items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());
    }

    @Test
    void updateItemQuantity_ShouldReturn204() throws Exception {
        String json = String.format("{\"productId\":%d,\"offerId\":%d}", productId, offerId);
        mockMvc.perform(post("/api/v1/users/{userId}/cart/items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        String updateJson = String.format("{\"productId\":%d,\"offerId\":%d,\"quantity\":3}", productId, offerId);
        mockMvc.perform(put("/api/v1/users/{userId}/cart/items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeItem_ShouldReturn204() throws Exception {
        String json = String.format("{\"productId\":%d,\"offerId\":%d}", productId, offerId);
        mockMvc.perform(post("/api/v1/users/{userId}/cart/items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(delete("/api/v1/users/{userId}/cart/items/{productId}/{offerId}", userId, productId, offerId))
                .andExpect(status().isNoContent());
    }

    @Test
    void removeItem_ShouldReturn404_cart_not_found() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{userId}/cart/items/{productId}/{offerId}", userId, productId, offerId))
                .andExpect(status().isNotFound());
    }

    @Test
    void addItem_ExceedStock_ShouldReturn409() throws Exception {
        String json = String.format("{\"productId\":%d,\"offerId\":%d}", productId, offerId);

        // Ajouter plus que le stock disponible (stock = 5)
        for (int i = 0; i < 5; i++) {
            mockMvc.perform(post("/api/v1/users/{userId}/cart/items", userId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isCreated());
        }

        // 6ème ajout => stock insuffisant
        mockMvc.perform(post("/api/v1/users/{userId}/cart/items", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());
    }
}
