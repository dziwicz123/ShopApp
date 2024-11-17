package com.example.shopapp.config.model;

public class ApiResponse {
    private String message;
    private boolean status;
    private User user;
    private Long basketId;

    // Getters and Setters
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isStatus() { return status; }
    public void setStatus(boolean status) { this.status = status; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Long getBasketId() { return basketId; }
    public void setBasketId(Long basketId) { this.basketId = basketId; }
}
