package com.example.shopapp.controller;

import com.example.shopapp.config.model.ApiResponse;
import com.example.shopapp.config.model.User;
import com.example.shopapp.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/api")
    public ApiResponse homeController() {
        ApiResponse res = new ApiResponse();
        res.setMessage("Welcome to API");
        res.setStatus(true);
        return res;
    }

    @PostMapping("/api/register")
    public ResponseEntity<User> registerUser(@RequestBody User user) {
        User savedUser = userService.createUser(user);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/api/login")
    public ResponseEntity<ApiResponse> loginUser(@RequestBody User loginRequest, HttpSession session) {
        User user = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        ApiResponse response = new ApiResponse();
        if (user != null) {
            session.setAttribute("user", user);
            response.setMessage("Login successful");
            response.setStatus(true);
            response.setUser(user); // Set user in response

            return ResponseEntity.ok(response);
        } else {
            response.setMessage("Invalid email or password");
            response.setStatus(false);
            return ResponseEntity.status(401).body(response);
        }
    }

    @GetMapping("/api/logout")
    public ResponseEntity<ApiResponse> logoutUser(HttpSession session) {
        session.invalidate();
        ApiResponse response = new ApiResponse();
        response.setMessage("Logout successful");
        response.setStatus(true);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/api/user")
    public ResponseEntity<User> getUser(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.status(401).build();
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
