package com.municipal.service;

import com.municipal.entity.Comment;
import com.municipal.entity.Complaint;
import com.municipal.entity.User;
import com.municipal.repository.CommentRepository;
import com.municipal.repository.ComplaintRepository;
import com.municipal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CommentService {
    
    @Autowired
    private CommentRepository commentRepository;
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    // Add comment to complaint
    public Comment addComment(Long complaintId, Long authorId, String content, 
                            Boolean isInternal, Comment.CommentType commentType) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Citizens cannot add internal comments
        if (author.getRole().equals(User.Role.CITIZEN) && isInternal) {
            throw new RuntimeException("Citizens cannot add internal comments");
        }
        
        Comment comment = new Comment(content, complaint, author, commentType);
        comment.setIsInternal(isInternal);
        
        return commentRepository.save(comment);
    }
    
    // Get comments for complaint (with visibility control)
    public List<Comment> getCommentsForComplaint(Long complaintId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Staff and Admin can see internal comments
        boolean includeInternal = user.getRole().equals(User.Role.STAFF) || user.getRole().equals(User.Role.ADMIN);
        
        return commentRepository.findByComplaintIdAndVisibility(complaintId, includeInternal);
    }
    
    // Get all comments for complaint (admin only)
    public List<Comment> getAllCommentsForComplaint(Long complaintId) {
        return commentRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId);
    }
    
    // Get public comments only
    public List<Comment> getPublicCommentsForComplaint(Long complaintId) {
        return commentRepository.findPublicCommentsByComplaintId(complaintId);
    }
    
    // Get internal comments only
    public List<Comment> getInternalCommentsForComplaint(Long complaintId) {
        return commentRepository.findInternalCommentsByComplaintId(complaintId);
    }
    
    // Get comment by ID
    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }
    
    // Update comment (only by author)
    public Comment updateComment(Long commentId, Long userId, String newContent) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Only the author can update their comment
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("You can only update your own comments");
        }
        
        comment.setContent(newContent);
        return commentRepository.save(comment);
    }
    
    // Delete comment (only by author or admin)
    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Only the author or admin can delete comment
        if (!comment.getAuthor().getId().equals(userId) && !user.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("You can only delete your own comments or you must be an admin");
        }
        
        commentRepository.delete(comment);
    }
    
    // Get comments by author
    public List<Comment> getCommentsByAuthor(Long authorId) {
        return commentRepository.findByAuthorIdOrderByCreatedAtDesc(authorId);
    }
    
    // Count comments for complaint
    public Long getCommentCountForComplaint(Long complaintId) {
        return commentRepository.countByComplaintId(complaintId);
    }
    
    // Check if user can view comment
    public boolean canUserViewComment(Long commentId, Long userId) {
        Optional<Comment> commentOpt = commentRepository.findById(commentId);
        if (!commentOpt.isPresent()) {
            return false;
        }
        
        Comment comment = commentOpt.get();
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // If it's an internal comment, only staff and admin can view
        if (comment.getIsInternal() && user.getRole().equals(User.Role.CITIZEN)) {
            return false;
        }
        
        // Citizens can only view comments on their own complaints
        if (user.getRole().equals(User.Role.CITIZEN)) {
            return comment.getComplaint().getCitizen().getId().equals(userId);
        }
        
        // Staff and Admin can view all comments
        return user.getRole().equals(User.Role.STAFF) || user.getRole().equals(User.Role.ADMIN);
    }
    
    // Check if user can add comment to complaint
    public boolean canUserAddComment(Long complaintId, Long userId) {
        Optional<Complaint> complaintOpt = complaintRepository.findById(complaintId);
        if (!complaintOpt.isPresent()) {
            return false;
        }
        
        Complaint complaint = complaintOpt.get();
        Optional<User> userOpt = userRepository.findById(userId);
        if (!userOpt.isPresent()) {
            return false;
        }
        
        User user = userOpt.get();
        
        // Citizens can only comment on their own complaints
        if (user.getRole().equals(User.Role.CITIZEN)) {
            return complaint.getCitizen().getId().equals(userId);
        }
        
        // Staff and Admin can comment on any complaint
        return user.getRole().equals(User.Role.STAFF) || user.getRole().equals(User.Role.ADMIN);
    }
    
    // Get recent comments for complaint
    public List<Comment> getRecentCommentsForComplaint(Long complaintId, int limit) {
        List<Comment> comments = commentRepository.findRecentCommentsByComplaintId(complaintId);
        return comments.size() > limit ? comments.subList(0, limit) : comments;
    }
}