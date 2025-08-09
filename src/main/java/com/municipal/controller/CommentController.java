package com.municipal.controller;

import com.municipal.entity.Comment;
import com.municipal.security.UserPrincipal;
import com.municipal.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/comments")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    // Add comment to complaint
    @PostMapping("/add")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> addComment(
            @Valid @RequestBody CommentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            if (!commentService.canUserAddComment(request.getComplaintId(), currentUser.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied");
                return ResponseEntity.status(403).body(error);
            }
            
            Comment.CommentType commentType = Comment.CommentType.GENERAL;
            if (request.getCommentType() != null) {
                commentType = Comment.CommentType.valueOf(request.getCommentType().toUpperCase());
            }
            
            // Citizens cannot add internal comments
            boolean isInternal = request.getIsInternal() != null ? request.getIsInternal() : false;
            
            Comment comment = commentService.addComment(
                    request.getComplaintId(),
                    currentUser.getId(),
                    request.getContent(),
                    isInternal,
                    commentType
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Comment added successfully");
            response.put("comment", comment);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get comments for complaint
    @GetMapping("/complaint/{complaintId}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getCommentsForComplaint(
            @PathVariable Long complaintId,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            List<Comment> comments = commentService.getCommentsForComplaint(complaintId, currentUser.getId());
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get comment by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getComment(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            if (!commentService.canUserViewComment(id, currentUser.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied");
                return ResponseEntity.status(403).body(error);
            }
            
            Optional<Comment> comment = commentService.getCommentById(id);
            if (comment.isPresent()) {
                return ResponseEntity.ok(comment.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Comment not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Update comment
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> updateComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            Comment comment = commentService.updateComment(id, currentUser.getId(), request.getContent());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Comment updated successfully");
            response.put("comment", comment);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Delete comment
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> deleteComment(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            commentService.deleteComment(id, currentUser.getId());
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Comment deleted successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get comments by author
    @GetMapping("/author/{authorId}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Comment>> getCommentsByAuthor(@PathVariable Long authorId) {
        List<Comment> comments = commentService.getCommentsByAuthor(authorId);
        return ResponseEntity.ok(comments);
    }
    
    // Get public comments for complaint
    @GetMapping("/complaint/{complaintId}/public")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Comment>> getPublicCommentsForComplaint(@PathVariable Long complaintId) {
        List<Comment> comments = commentService.getPublicCommentsForComplaint(complaintId);
        return ResponseEntity.ok(comments);
    }
    
    // Get internal comments for complaint (Staff/Admin only)
    @GetMapping("/complaint/{complaintId}/internal")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Comment>> getInternalCommentsForComplaint(@PathVariable Long complaintId) {
        List<Comment> comments = commentService.getInternalCommentsForComplaint(complaintId);
        return ResponseEntity.ok(comments);
    }
    
    // Get comment count for complaint
    @GetMapping("/complaint/{complaintId}/count")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Map<String, Long>> getCommentCountForComplaint(@PathVariable Long complaintId) {
        Long count = commentService.getCommentCountForComplaint(complaintId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
    
    // Request DTOs
    public static class CommentRequest {
        private Long complaintId;
        private String content;
        private Boolean isInternal;
        private String commentType;
        
        public Long getComplaintId() { return complaintId; }
        public void setComplaintId(Long complaintId) { this.complaintId = complaintId; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public Boolean getIsInternal() { return isInternal; }
        public void setIsInternal(Boolean isInternal) { this.isInternal = isInternal; }
        public String getCommentType() { return commentType; }
        public void setCommentType(String commentType) { this.commentType = commentType; }
    }
    
    public static class CommentUpdateRequest {
        private String content;
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
}