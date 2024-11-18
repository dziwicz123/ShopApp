package com.example.shopapp.controller;

import com.example.shopapp.service.StripeService;
import com.stripe.model.checkout.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payment")
public class StripeController {

    @Autowired
    private StripeService stripeService;

    @PostMapping("/create-payment-intent")
    public ResponseEntity<?> createPaymentIntent(@RequestBody CheckoutRequest request) {
        try {
            String clientSecret = stripeService.createPaymentIntent(request.getAmount(), request.getCurrency());
            return ResponseEntity.ok(new CheckoutResponse(clientSecret));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error creating payment intent");
        }
    }

    public static class CheckoutRequest {
        private long amount;
        private String currency;

        // Getters and setters

        public long getAmount() {
            return amount;
        }

        public void setAmount(long amount) {
            this.amount = amount;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }
    }

    public static class CheckoutResponse {
        private String clientSecret;

        public CheckoutResponse(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        // Getter

        public String getClientSecret() {
            return clientSecret;
        }
    }
}

