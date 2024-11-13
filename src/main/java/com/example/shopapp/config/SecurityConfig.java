package com.example.shopapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors().and()
                .csrf().disable()
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/login",
                                "/register",
                                "/api/register",
                                "/api/login",
                                "/api/categories",
                                "/api/categories/**",
                                "/api/products",
                                "/api/products/add",
                                "/api/products/**",
                                "/api/products/search",
                                "/api/basket",
                                "/api/basket/user/**",
                                "/api/basket/addProduct",
                                "/api/basket/add",
                                "/api/order",
                                "/api/order/**",
                                "/api/order/update/**",
                                "/api/users",
                                "/api/users/**",
                                "/api/payment",
                                "/api/payment/create-checkout-session",
                                "/api/shop",
                                "/api/shop/**",
                                "/api/comments",
                                "/api/comments/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
                        .invalidSessionUrl("/login")
                );

        return http.build();
    }
}
