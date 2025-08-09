package com.example.complaints.service;

import com.example.complaints.model.Complaint;
import com.example.complaints.model.ComplaintComment;
import com.example.complaints.repository.ComplaintCommentRepository;
import com.example.complaints.repository.ComplaintRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class CommentService {
    private final ComplaintRepository complaintRepository;
    private final ComplaintCommentRepository commentRepository;

    public CommentService(ComplaintRepository complaintRepository, ComplaintCommentRepository commentRepository) {
        this.complaintRepository = complaintRepository;
        this.commentRepository = commentRepository;
    }

    public ComplaintComment addComment(Long complaintId, Long authorId, String authorRole, @Valid String message) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new IllegalArgumentException("Complaint not found"));
        ComplaintComment comment = new ComplaintComment();
        comment.setComplaint(complaint);
        comment.setAuthorId(authorId);
        comment.setAuthorRole(authorRole);
        comment.setMessage(message);
        comment.setCreatedAt(Instant.now());
        return commentRepository.save(comment);
    }

    public List<ComplaintComment> listComments(Long complaintId) {
        return commentRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId);
    }
}