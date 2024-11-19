package com.example.shopapp.controller;

import com.example.shopapp.config.model.Comment;
import com.example.shopapp.config.model.Product;
import com.example.shopapp.config.model.User;
import com.example.shopapp.repo.CommentRepository;
import com.example.shopapp.repo.ProductRepository;
import com.example.shopapp.repo.UserRepository;
import com.example.shopapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.shopapp.dto.CommentDTO;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CommentRepository commentRepository;

    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Map<String, Object> payload) {
        try {
            Long productId = Long.valueOf(payload.get("productId").toString());
            Long userId = Long.valueOf(payload.get("userId").toString());
            int rating = Integer.parseInt(payload.get("rating").toString());
            String description = payload.get("description").toString();

            Optional<Product> productOpt = productRepository.findById(productId);
            Optional<User> userOpt = userRepository.findById(userId);

            if (!productOpt.isPresent() || !userOpt.isPresent()) {
                return ResponseEntity.status(404).body("Produkt lub użytkownik nie istnieje");
            }

            Product product = productOpt.get();
            User user = userOpt.get();

            Comment comment = new Comment();
            comment.setProduct(product);
            comment.setUser(user);
            comment.setRating(rating);
            comment.setDescription(description);

            commentRepository.save(comment);

            return ResponseEntity.ok("Komentarz został dodany");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Błąd podczas dodawania komentarza");
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByProductId(@PathVariable Long productId) {
        List<CommentDTO> comments = commentService.getCommentsByProductId(productId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateComment(@PathVariable Long id, @RequestBody CommentDTO commentDTO) {
        Optional<Comment> existingComment = commentRepository.findById(id);
        if (!existingComment.isPresent()) {
            return ResponseEntity.status(404).body("Komentarz nie istnieje");
        }

        Comment comment = existingComment.get();
        comment.setRating(commentDTO.getRating());
        comment.setDescription(commentDTO.getDescription());
        commentRepository.save(comment);

        return ResponseEntity.ok("Komentarz został zaktualizowany");
    }

    @GetMapping("/user/{userEmail}")
    public ResponseEntity<List<CommentDTO>> getCommentsByUserEmail(@PathVariable String userEmail) {
        List<CommentDTO> comments = commentService.getCommentsByUserEmail(userEmail);
        return ResponseEntity.ok(comments);
    }
}