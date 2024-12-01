package com.example.shopapp.config.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "order_issues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class OrderIssue {
    @Id
    @Column(name = "order_issue_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private OrderIssueType orderIssueType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "order_details_id", nullable = false)
    @JsonBackReference
    private OrderDetails orderDetails;
}

