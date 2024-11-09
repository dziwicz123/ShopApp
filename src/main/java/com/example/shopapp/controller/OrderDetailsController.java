package com.example.shopapp.controller;

import com.example.shopapp.DTO.OrderDetailsRequest;
import com.example.shopapp.config.model.*;
import com.example.shopapp.repo.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/order")
public class OrderDetailsController {

    private static final Logger logger = LoggerFactory.getLogger(OrderDetailsController.class);

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private OrderDetailsRepository orderDetailsRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<?> createOrderDetails(@RequestBody OrderDetailsRequest request) {
        logger.info("Received order creation request for email: {}", request.getEmail());

        // 1. Validate User
        User user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            logger.error("User not authenticated for email: {}", request.getEmail());
            return ResponseEntity.status(401).body("User not authenticated");
        }
        logger.info("Authenticated user: {}", user.getEmail());

        // 2. Validate Basket
        Optional<Basket> optionalBasket = basketRepository.findById(request.getBasketId());
        if (!optionalBasket.isPresent() || !optionalBasket.get().getUser().equals(user)) {
            logger.error("Basket not found or does not belong to user: {} with basketId: {}", user.getEmail(), request.getBasketId());
            return ResponseEntity.status(404).body("Basket not found or does not belong to the user");
        }
        Basket basket = optionalBasket.get();
        logger.info("Retrieved basket for user: {}", user.getEmail());

        // 3. Create and Save Address
        Address address = new Address();
        try {
            address.setCity(request.getAddress().getCity());
            address.setStreet(request.getAddress().getStreet());
            address.setPostalCode(request.getAddress().getPostalCode());
            address.setUser(user);
            addressRepository.save(address);
            logger.info("Address saved successfully for user: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Error saving address for user: {}", user.getEmail(), e);
            return ResponseEntity.status(500).body("Failed to save address");
        }

        // 4. Create Order Details
        OrderDetails orderDetails = new OrderDetails();
        orderDetails.setBasket(basket);
        orderDetails.setOrderDate(LocalDateTime.now());
        orderDetails.setShipDate(null); // Set if applicable
        orderDetails.setState(OrderState.PENDING);
        orderDetails.setType(PaymentStatus.UNPAID);
        orderDetails.setAddress(address); // Set the saved address directly

        // Save Order Details
        try {
            orderDetailsRepository.save(orderDetails);
            logger.info("Order details saved successfully for user: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Error saving order details for user: {}", user.getEmail(), e);
            return ResponseEntity.status(500).body("Failed to save order details");
        }

        // 5. Add Products to Basket and Calculate Total Price
        float totalPrice = 0.0f;
        for (OrderDetailsRequest.ProductDTO productDTO : request.getProducts()) {
            Optional<Product> optionalProduct = productRepository.findById(productDTO.getProductId());
            if (optionalProduct.isPresent()) {
                Product product = optionalProduct.get();
                int quantity = productDTO.getQuantity();

                // Check if the product already exists in the basket
                Optional<BasketProduct> optionalBasketProduct = basket.getBasketProducts().stream()
                        .filter(bp -> bp.getProduct().getId().equals(product.getId()))
                        .findFirst();

                BasketProduct basketProduct;
                if (optionalBasketProduct.isPresent()) {
                    basketProduct = optionalBasketProduct.get();
                    basketProduct.setQuantity(basketProduct.getQuantity() + quantity);
                } else {
                    basketProduct = new BasketProduct();
                    basketProduct.setBasket(basket);
                    basketProduct.setProduct(product);
                    basketProduct.setQuantity(quantity);
                    basket.getBasketProducts().add(basketProduct);
                }

                totalPrice += product.getPrice() * quantity;
                logger.info("Added product {} to basket for user {}", product.getProductName(), user.getEmail());
            } else {
                logger.warn("Product with ID {} not found for user {}", productDTO.getProductId(), user.getEmail());
            }
        }
        basket.setTotalPrice(totalPrice);
        basket.setState(true);

        // Save updated Basket
        try {
            basketRepository.save(basket);
            logger.info("Basket updated and saved successfully for user: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Error saving updated basket for user: {}", user.getEmail(), e);
            return ResponseEntity.status(500).body("Failed to save updated basket");
        }

        // 6. Create a new Basket for the User
        try {
            Basket newBasket = new Basket();
            newBasket.setUser(user);
            newBasket.setState(false);
            newBasket.setTotalPrice(0.0f);
            basketRepository.save(newBasket);
            logger.info("New basket created for user: {}", user.getEmail());
        } catch (Exception e) {
            logger.error("Error creating new basket for user: {}", user.getEmail(), e);
            return ResponseEntity.status(500).body("Failed to create new basket");
        }

        return ResponseEntity.ok(orderDetails);
    }

    @PatchMapping("/update-payment-status/{basketId}")
    public ResponseEntity<?> updatePaymentStatus(@PathVariable Long basketId, @RequestBody Map<String, String> payload) {
        String paymentStatus = payload.get("paymentStatus");
        Optional<Basket> optionalBasket = basketRepository.findById(basketId);

        if (!optionalBasket.isPresent()) {
            logger.error("Basket not found with ID: {}", basketId);
            return ResponseEntity.status(404).body("Basket not found");
        }

        Basket basket = optionalBasket.get();
        OrderDetails orderDetails = orderDetailsRepository.findByBasket(basket);

        if (orderDetails == null) {
            logger.error("Order details not found for basket ID: {}", basketId);
            return ResponseEntity.status(404).body("OrderDetails not found for the given basket");
        }

        if ("PAID".equals(paymentStatus)) {
            orderDetails.setType(PaymentStatus.PAID);
            orderDetailsRepository.save(orderDetails);
            logger.info("Payment status updated to PAID for basket ID: {}", basketId);
            return ResponseEntity.ok(orderDetails);
        } else {
            logger.warn("Invalid payment status received: {}", paymentStatus);
            return ResponseEntity.status(400).body("Invalid payment status");
        }
    }

    @GetMapping("/all")
    public List<OrderDetails> getAllOrders() {
        return orderDetailsRepository.findAll();
    }

    @PatchMapping("/update/{id}")
    public ResponseEntity<OrderDetails> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, OrderState> payload) {
        OrderState state = payload.get("state");
        Optional<OrderDetails> optionalOrder = orderDetailsRepository.findById(id);
        if (!optionalOrder.isPresent()) {
            logger.error("Order not found with ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        OrderDetails orderDetails = optionalOrder.get();
        orderDetails.setState(state);
        if (state == OrderState.SHIPPED) {
            orderDetails.setShipDate(LocalDateTime.now());
        }
        orderDetailsRepository.save(orderDetails);
        logger.info("Order status updated to {} for order ID: {}", state, id);
        return ResponseEntity.ok(orderDetails);
    }
}
