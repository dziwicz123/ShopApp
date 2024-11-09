package com.example.shopapp.service;

import com.example.shopapp.config.model.Basket;
import com.example.shopapp.config.model.User;
import com.example.shopapp.config.model.UserType;
import com.example.shopapp.repo.BasketRepository;
import com.example.shopapp.repo.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final BasketRepository basketRepository;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, BasketRepository basketRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.basketRepository = basketRepository;
    }

    public boolean checkLogin(User user) {
        User foundUser = userRepository.findByEmail(user.getEmail());
        return foundUser != null && passwordEncoder.matches(user.getPassword(), foundUser.getPassword());
    }

    public User createUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setUserType(UserType.USER);
        User savedUser = userRepository.save(user);

        // Create and save a basket for the new user
        Basket basket = new Basket();
        basket.setUser(savedUser);
        basket.setState(false);
        basket.setTotalPrice(0.0f);
        basketRepository.save(basket);

        return savedUser;
    }

    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null && passwordEncoder.matches(password, user.getPassword())) {
            // Fetch the basket with state = false
            Basket basket = basketRepository.findByUserAndState(user, false);
            user.setBaskets(Collections.singletonList(basket)); // Assuming there's only one such basket
            return user;
        }
        return null;
    }




    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        basketRepository.deleteAll(user.getBaskets());
        userRepository.deleteById(id);
    }
}
