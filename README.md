# Backend Panier E-commerce – Spring Boot

## 1. Présentation

Backend Java 17 + Spring Boot pour gérer les paniers d’une plateforme e‑commerce multi-utilisateurs.  

Fonctionnalités principales :  

- **Gestion du panier**: (Ajout, modification de quantité et suppression)
- **Checkout** : Validation de commande avec mise à jour du stock en temps réel.
- **Moteur de Prix** : Calcul des remises et des totaux au niveau du domaine. 
- Architecture **Controller → Service → Repository**, et utilisation de **DTOs**  

---

## 2. Architecture et Environnements
L'application utilise les Spring Profiles pour s'adapter à l'environnement :

| Profil                              | Usage               | Base de Données                                            | Fichier de propriété        |
|-------------------------------------|---------------------|------------------------------------------------------------|-----------------------------|
| dev                                 | Développement local | H2                                                         | application-dev.properties  |
| test                                | Tests d'intégration | H2                                                         | application-test.properties |
| prod                                | Production          | PostgresSQL                                                | application-prod.properties |
---
## 3. Lancer l’application

1. Cloner le projet :  
```bash
git clone https://gitlab.com/eperiana/test-tech
cd test-tech/api
```

2. Compiler et lancer avec Maven :
```
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```
L’application démarre par défaut sur http://localhost:8080

3. Lancer les tests
Exécute la suite complète (Unitaires + Intégration) :
```
./mvnw test
```

---
## 4. Endpoints API

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
## 5. Intégrité des Données
Le modèle de données impose des règles strictes via JPA et le schéma SQL :
- Remises : discount_percent doit être compris entre 0 et 100. 
- Stocks : stock_qty ne peut jamais être inférieur à 0.
---
## 6. Stratégie de Test
Le projet suit une pyramide de tests :
- Tests Unitaires (JUnit 5 + Mockito) : Validation de la logique métier (getFinalUnitPrice, calculs totaux) et des services.
- Tests d'intégrité (DataJpaTest) : Vérification des contraintes de base de données (Check constraints, Nullability).
- Tests d'Intégration (MockMvc) : Validation des contrats API et des codes de retour HTTP sur des scénarios complets.
--- 
## 7. Outils & Documentation
- Swagger UI : http://localhost:8080/swagger-ui.html
- Console H2 (en profil dev) : http://localhost:8080/h2-console
--- 
# 8. Notes Techniques
- Java 17 + Spring Boot 3 
- Lombok : Pour un code concis. 
- Flyway : Pour le versioning SQL en production. 
- Text Blocks : Utilisés dans les tests pour une meilleure lisibilité du JSON.