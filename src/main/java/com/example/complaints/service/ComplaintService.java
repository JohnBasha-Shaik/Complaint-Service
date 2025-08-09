package com.example.complaints.service;

import com.example.complaints.model.Complaint;
import com.example.complaints.model.ComplaintComment;
import com.example.complaints.repository.ComplaintCommentRepository;
import com.example.complaints.repository.ComplaintRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintCommentRepository commentRepository;

    public ComplaintService(ComplaintRepository complaintRepository, ComplaintCommentRepository commentRepository) {
        this.complaintRepository = complaintRepository;
        this.commentRepository = commentRepository;
    }

    public Complaint createComplaint(@Valid Complaint complaint) {
        complaint.setId(null);
        complaint.setStatus("NEW");
        complaint.setCreatedAt(Instant.now());
        complaint.setUpdatedAt(Instant.now());
        return complaintRepository.save(complaint);
    }

    public List<Complaint> listCitizenComplaints(Long citizenId) {
        return complaintRepository.findByCitizenIdOrderByCreatedAtDesc(citizenId);
    }

    public Optional<Complaint> getComplaint(Long id) {
        return complaintRepository.findById(id);
    }

    @Transactional
    public Complaint assignDepartment(Long complaintId, String department, Long staffUserId) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        complaint.setAssignedDepartment(department);
        complaint.setStatus("ASSIGNED");
        complaint.setUpdatedAt(Instant.now());

        ComplaintComment log = new ComplaintComment();
        log.setComplaint(complaint);
        log.setAuthorId(staffUserId);
        log.setAuthorRole("STAFF");
        log.setMessage("Assigned to department: " + department);
        log.setCreatedAt(Instant.now());
        commentRepository.save(log);

        return complaintRepository.save(complaint);
    }

    public List<Complaint> listUnassignedComplaints() {
        return complaintRepository.findByAssignedDepartmentIsNullOrderByCreatedAtAsc();
    }
}