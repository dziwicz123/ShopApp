package com.example.shopapp.controller;

import com.example.shopapp.config.model.Comment;
import com.example.shopapp.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;
    @GetMapping("/product/{productId}")
    public List<Comment> getCommentsByProductId(@PathVariable Long productId) {
        return commentService.getCommentsByProductId(productId);
    }
}
