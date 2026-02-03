# Backend Panier E-commerce – Spring Boot

## 1️. Présentation

Backend Java 17 + Spring Boot pour gérer les paniers d’une plateforme e‑commerce multi-utilisateurs.  

Fonctionnalités principales :  

- Gestion des paniers utilisateurs (ajout, modification, suppression d’articles)  
- Passage de commande (checkout) avec décrémentation du stock  
- Calcul des prix unitaires et du total du panier  
- Stockage **en mémoire** (base h2)  
- Architecture **Controller → Service → Repository**, et utilisation de **DTOs**  

---

## 2️. Lancer l’application

1. Cloner le projet :  
```bash
git clone https://gitlab.com/eperiana/test-tech-darty
cd test-tech-darty/api
```

2. Compiler et lancer avec Maven :
```
./mvnw spring-boot:run
```
L’application démarre par défaut sur http://localhost:8080

---
# 3️. Endpoints API

Préfixe commun :
```
/api/v1/users/{userId}
```
| Endpoint                            | Méthode | Description                                                | Body                                                      | Codes retour / Exceptions                                                            |
| ----------------------------------- | ------- | ---------------------------------------------------------- | --------------------------------------------------------- | ------------------------------------------------------------------------------------ |
| `/cart`                             | GET     | Récupérer le panier de l’utilisateur                       | –                                                         | `200 OK`                                                                             |
| `/cart/items`                       | POST    | Ajouter un article au panier (quantité = 1 par défaut)     | `{ "productId": Long, "offerId": Long }`                  | `200 OK`, `InsufficientStockException` si stock insuffisant                          |
| `/cart/items`                       | PUT     | Modifier la quantité d’un article du panier                | `{ "productId": Long, "offerId": Long, "quantity": int }` | `200 OK`, `CartLineNotFoundException`,`CartNotFoundException`, `InsufficientStockException`                  |
| `/cart/items/{productId}/{offerId}` | DELETE  | Supprimer une ligne du panier                              | –                                                         | `200 OK`                                                                             |
| `/checkout`                         | POST    | Passer la commande : décrémente le stock et vide le panier | –                                                         | `200 OK`, `InsufficientStockException` si stock insuffisant, `CartNotFoundException` |

---

# 4️. Configuration du stock

Le stock est initialisé en mémoire via la classe DataInitializer.


--- 
# 5️. Structure des DTOs

- AddItemRequestDto : { productId, offerId }
- UpdateItemRequestDto : { productId, offerId, quantity }
- CartLineResponseDto : { productId, offerId, state, quantity, unitPrice, lineTotal }
- CartResponseDto : { userId, lines, totalPrice }

---
# 6. Swagger UI

Pour explorer et tester l’API directement depuis un navigateur :

http://localhost:8080/swagger-ui.html

--- 
# 7. Notes techniques
- Java 17 + Spring Boot 3
- Stockage en mémoire uniquement (h2)
- Architecture couche Controller → Service → Repository
- Gestion des exceptions via @ControllerAdvice
- DTOs pour tous les échanges REST