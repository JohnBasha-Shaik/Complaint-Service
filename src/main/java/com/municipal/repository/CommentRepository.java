package com.municipal.repository;

import com.municipal.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // Find comments by complaint
    List<Comment> findByComplaintIdOrderByCreatedAtAsc(Long complaintId);
    
    // Find comments by complaint (excluding internal comments for citizens)
    @Query("SELECT c FROM Comment c WHERE c.complaint.id = :complaintId AND " +
           "(:includeInternal = true OR c.isInternal = false) " +
           "ORDER BY c.createdAt ASC")
    List<Comment> findByComplaintIdAndVisibility(@Param("complaintId") Long complaintId, 
                                                @Param("includeInternal") Boolean includeInternal);
    
    // Find comments by author
    List<Comment> findByAuthorIdOrderByCreatedAtDesc(Long authorId);
    
    // Find internal comments only
    @Query("SELECT c FROM Comment c WHERE c.complaint.id = :complaintId AND c.isInternal = true ORDER BY c.createdAt ASC")
    List<Comment> findInternalCommentsByComplaintId(@Param("complaintId") Long complaintId);
    
    // Find public comments only
    @Query("SELECT c FROM Comment c WHERE c.complaint.id = :complaintId AND c.isInternal = false ORDER BY c.createdAt ASC")
    List<Comment> findPublicCommentsByComplaintId(@Param("complaintId") Long complaintId);
    
    // Count comments by complaint
    Long countByComplaintId(Long complaintId);
    
    // Find comments by type
    List<Comment> findByCommentTypeOrderByCreatedAtDesc(Comment.CommentType commentType);
    
    // Find recent comments for a complaint
    @Query("SELECT c FROM Comment c WHERE c.complaint.id = :complaintId ORDER BY c.createdAt DESC")
    List<Comment> findRecentCommentsByComplaintId(@Param("complaintId") Long complaintId);
}