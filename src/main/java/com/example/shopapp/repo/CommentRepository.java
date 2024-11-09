package com.example.shopapp.repo;

import org.springframework.stereotype.Repository;
import com.example.shopapp.config.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

@Repository
public interface CommentRepository  extends JpaRepository<Comment, Long>{
    List<Comment> findByProductId(Long productId);

}
