package com.example.complaints.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "complaint_comments")
@Getter
@Setter
@NoArgsConstructor
public class ComplaintComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @NotNull
    private Long authorId;

    @NotBlank
    @Column(length = 2000)
    private String message;

    @NotBlank
    private String authorRole; // CITIZEN or STAFF

    private Instant createdAt = Instant.now();
}