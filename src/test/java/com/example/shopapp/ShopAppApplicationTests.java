package com.example.shopapp;

import com.example.shopapp.config.model.*;
import com.example.shopapp.repo.*;
import com.github.javafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

@SpringBootTest
class ShopAppApplicationTests {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Test
    void contextLoads() {
        Faker faker = new Faker();
        Random random = new Random();

        // Pobierz listę produktów o ID od 1 do 24
        List<Long> productIds = LongStream.rangeClosed(1, 24).boxed().collect(Collectors.toList());
        List<Product> products = productRepository.findAllById(productIds);

        Map<Long, Product> productMap = products.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));

        // Tworzenie 100 użytkowników
        List<User> users = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = User.builder()
                    .name(faker.name().firstName())
                    .lastName(faker.name().lastName())
                    .email(faker.internet().emailAddress())
                    .phone(faker.number().numberBetween(100000000, 999999999))
                    .password(faker.internet().password())
                    .userType(UserType.USER)
                    .build();

            user = userRepository.save(user);
            users.add(user);
        }

        // Mapowanie produktów do odpowiednich dystrybucji ocen
        Map<Long, double[]> productRatingDistributions = new HashMap<>();

        // Produkty 1-6: średnia ocen 2.5-3.0
        double[] distribution1 = {0, 0.05, 0.3, 0.5, 0.15, 0};
        for (long i = 1; i <= 6; i++) {
            productRatingDistributions.put(i, distribution1);
        }

        // Produkty 7-14: średnia ocen 3.5-4.0
        double[] distribution2 = {0, 0, 0.1, 0.35, 0.35, 0.2};
        for (long i = 7; i <= 14; i++) {
            productRatingDistributions.put(i, distribution2);
        }

        // Produkty 15-22: średnia ocen 4.0-4.5
        double[] distribution3 = {0, 0, 0, 0.2, 0.2, 0.6};
        for (long i = 15; i <= 22; i++) {
            productRatingDistributions.put(i, distribution3);
        }

        // Produkty 23-24: średnia ocen 4.5-5.0
        double[] distribution4 = {0, 0, 0, 0.1, 0.1, 0.8};
        for (long i = 23; i <= 24; i++) {
            productRatingDistributions.put(i, distribution4);
        }

        // Generowanie ocen dla każdego produktu
        for (Product product : products) {
            long productId = product.getId();
            double[] distribution = productRatingDistributions.get(productId);

            if (distribution != null) {
                int numberOfComments = 40; // Możesz dostosować liczbę komentarzy

                for (int k = 0; k < numberOfComments; k++) {
                    // Wybór losowego użytkownika
                    User user = users.get(random.nextInt(users.size()));

                    int rating = generateRating(distribution, random); // Generowanie oceny na podstawie dystrybucji
                    String description = faker.lorem().sentence();

                    Comment comment = Comment.builder()
                            .rating(rating)
                            .description(description)
                            .product(product)
                            .user(user)
                            .build();

                    commentRepository.save(comment);
                }
            }
        }

        // Obliczanie średniej ocen dla każdego produktu
        calculateAverageRatingsForProducts(products);

        System.out.println("Dane testowe zostały pomyślnie zainicjalizowane.");
    }

    // Funkcja obliczająca średnią ocen dla każdego produktu
    private void calculateAverageRatingsForProducts(List<Product> products) {
        for (Product product : products) {
            List<Comment> comments = commentRepository.findByProductId(product.getId());

            if (comments != null && !comments.isEmpty()) {
                double averageRating = comments.stream()
                        .mapToInt(Comment::getRating)
                        .average()
                        .orElse(0.0);

                // Aktualizacja średniej oceny produktu
                product.setRating((float) averageRating);
                productRepository.save(product);

                System.out.println("Produkt ID: " + product.getId() + ", Średnia ocena: " + averageRating);
            } else {
                System.out.println("Produkt ID: " + product.getId() + " nie ma żadnych ocen.");
            }
        }
    }

    // Funkcja pomocnicza do generowania oceny na podstawie dystrybucji
    private int generateRating(double[] distribution, Random random) {
        double p = random.nextDouble();
        double cumulative = 0.0;
        for (int i = 1; i <= 5; i++) {
            cumulative += distribution[i];
            if (p <= cumulative) {
                return i;
            }
        }
        return 5; // W przypadku błędów zaokrąglenia
    }
}
