package com.example.shopapp.service;

import com.example.shopapp.config.model.Comment;
import com.example.shopapp.dto.CommentDTO;
import com.example.shopapp.repo.CommentRepository;
import com.example.shopapp.repo.ProductRepository;
import com.example.shopapp.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    // Pobieranie komentarzy dla produktu
    public List<CommentDTO> getCommentsByProductId(Long productId) {
        List<Comment> comments = commentRepository.findByProductId(productId);

        return comments.stream().map(comment -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(comment.getId());
            dto.setRating(comment.getRating());
            dto.setDescription(comment.getDescription());
            dto.setUsername(comment.getUser().getName());
            return dto;
        }).collect(Collectors.toList());
    }

    public List<CommentDTO> getCommentsByUserEmail(String userEmail) {
        List<Comment> comments = commentRepository.findByUserEmail(userEmail);

        return comments.stream().map(comment -> {
            CommentDTO dto = new CommentDTO();
            dto.setId(comment.getId());
            dto.setRating(comment.getRating());
            dto.setDescription(comment.getDescription());
            dto.setUsername(comment.getUser().getName()); // Zakładając, że User ma pole `name`
            dto.setProductName(comment.getProduct().getProductName()); // Jeśli potrzebne
            dto.setProductImage(comment.getProduct().getImage()); // Jeśli potrzebne
            return dto;
        }).collect(Collectors.toList());
    }
}

