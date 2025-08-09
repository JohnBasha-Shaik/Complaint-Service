package com.example.complaints.repository;

import com.example.complaints.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByCitizenIdOrderByCreatedAtDesc(Long citizenId);
    List<Complaint> findByAssignedDepartmentIsNullOrderByCreatedAtAsc();
}