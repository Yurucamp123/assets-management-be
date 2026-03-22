package com.example.iamsbe.models.entities;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;

@Entity
@Table(name = "requests")
@Data
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @ManyToOne
    @JoinColumn(name = "asset_id")
    private Asset asset;
    
    private LocalDate borrowDate;
    private LocalDate returnDate;
    private String status;
    private String rejectReason;

    @Column(updatable = false)
    @CreationTimestamp
    private java.time.LocalDateTime createdAt;
}