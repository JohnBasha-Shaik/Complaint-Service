package com.municipal.repository;

import com.municipal.entity.Complaint;
import com.municipal.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    
    // Find complaints by citizen
    Page<Complaint> findByCitizenId(Long citizenId, Pageable pageable);
    
    List<Complaint> findByCitizenOrderByCreatedAtDesc(User citizen);
    
    // Find complaints by status
    List<Complaint> findByStatus(Complaint.Status status);
    
    Page<Complaint> findByStatus(Complaint.Status status, Pageable pageable);
    
    // Find complaints by category
    Page<Complaint> findByCategory(Complaint.Category category, Pageable pageable);
    
    // Find complaints by department
    Page<Complaint> findByAssignedDepartmentId(Long departmentId, Pageable pageable);
    
    List<Complaint> findByAssignedDepartmentIdAndStatus(Long departmentId, Complaint.Status status);
    
    // Find unassigned complaints
    @Query("SELECT c FROM Complaint c WHERE c.assignedDepartment IS NULL AND c.status IN ('SUBMITTED', 'ACKNOWLEDGED')")
    List<Complaint> findUnassignedComplaints();
    
    @Query("SELECT c FROM Complaint c WHERE c.assignedDepartment IS NULL AND c.status IN ('SUBMITTED', 'ACKNOWLEDGED')")
    Page<Complaint> findUnassignedComplaints(Pageable pageable);
    
    // Find complaints by priority
    Page<Complaint> findByPriority(Complaint.Priority priority, Pageable pageable);
    
    // Find complaints by date range
    @Query("SELECT c FROM Complaint c WHERE c.createdAt BETWEEN :startDate AND :endDate")
    List<Complaint> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find complaints assigned to specific staff member
    Page<Complaint> findByAssignedStaffId(Long staffId, Pageable pageable);
    
    // Count complaints by status
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.status = :status")
    Long countByStatus(@Param("status") Complaint.Status status);
    
    // Count complaints by department
    @Query("SELECT COUNT(c) FROM Complaint c WHERE c.assignedDepartment.id = :departmentId")
    Long countByDepartment(@Param("departmentId") Long departmentId);
    
    // Find recent complaints
    @Query("SELECT c FROM Complaint c ORDER BY c.createdAt DESC")
    Page<Complaint> findRecentComplaints(Pageable pageable);
    
    // Dashboard queries
    @Query("SELECT c FROM Complaint c WHERE c.status = 'SUBMITTED' ORDER BY c.createdAt DESC")
    List<Complaint> findNewComplaints();
    
    @Query("SELECT c FROM Complaint c WHERE c.priority = 'URGENT' AND c.status NOT IN ('RESOLVED', 'CLOSED') ORDER BY c.createdAt ASC")
    List<Complaint> findUrgentComplaints();
    
    // Search complaints
    @Query("SELECT c FROM Complaint c WHERE " +
           "LOWER(c.description) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(c.citizen.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Complaint> searchComplaints(@Param("keyword") String keyword, Pageable pageable);
}