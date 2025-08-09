package com.example.complaints.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "complaints")
@Getter
@Setter
@NoArgsConstructor
public class Complaint {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String category; // water, sanitation, roads, other

    @NotBlank
    @Column(length = 2000)
    private String description;

    private String attachment; // file path or URL

    @NotNull
    private Long citizenId;

    @NotBlank
    private String status = "NEW"; // NEW, ASSIGNED, IN_PROGRESS, RESOLVED, CLOSED

    private String assignedDepartment;

    private Instant createdAt = Instant.now();

    private Instant updatedAt = Instant.now();

    @OneToMany(mappedBy = "complaint", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<ComplaintComment> comments = new ArrayList<>();
}