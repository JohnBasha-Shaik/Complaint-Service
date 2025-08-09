package com.municipal.service;

import com.municipal.entity.Comment;
import com.municipal.entity.Complaint;
import com.municipal.entity.Department;
import com.municipal.entity.User;
import com.municipal.repository.CommentRepository;
import com.municipal.repository.ComplaintRepository;
import com.municipal.repository.DepartmentRepository;
import com.municipal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ComplaintService {
    
    @Autowired
    private ComplaintRepository complaintRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private CommentRepository commentRepository;
    
    // Create new complaint
    public Complaint createComplaint(Complaint.Category category, String description, 
                                   String locationAddress, Long citizenId, MultipartFile attachment) {
        User citizen = userRepository.findById(citizenId)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));
        
        if (!citizen.getRole().equals(User.Role.CITIZEN)) {
            throw new RuntimeException("Only citizens can file complaints");
        }
        
        Complaint complaint = new Complaint(category, description, citizen);
        complaint.setLocationAddress(locationAddress);
        
        // Handle file attachment
        if (attachment != null && !attachment.isEmpty()) {
            try {
                String fileName = fileStorageService.storeFile(attachment);
                complaint.setAttachment(fileName);
            } catch (Exception e) {
                throw new RuntimeException("Failed to store attachment: " + e.getMessage());
            }
        }
        
        Complaint savedComplaint = complaintRepository.save(complaint);
        
        // Add initial comment
        Comment initialComment = new Comment("Complaint submitted successfully.", savedComplaint, citizen, Comment.CommentType.STATUS_UPDATE);
        initialComment.setIsInternal(false);
        commentRepository.save(initialComment);
        
        return savedComplaint;
    }
    
    // Get complaint by ID
    public Optional<Complaint> getComplaintById(Long id) {
        return complaintRepository.findById(id);
    }
    
    // Get complaints by citizen
    public Page<Complaint> getComplaintsByCitizen(Long citizenId, Pageable pageable) {
        return complaintRepository.findByCitizenId(citizenId, pageable);
    }
    
    // Get all complaints (for staff/admin)
    public Page<Complaint> getAllComplaints(Pageable pageable) {
        return complaintRepository.findAll(pageable);
    }
    
    // Get unassigned complaints
    public List<Complaint> getUnassignedComplaints() {
        return complaintRepository.findUnassignedComplaints();
    }
    
    public Page<Complaint> getUnassignedComplaints(Pageable pageable) {
        return complaintRepository.findUnassignedComplaints(pageable);
    }
    
    // Assign complaint to department
    public Complaint assignComplaintToDepartment(Long complaintId, Long departmentId, Long assignedBy) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        User assigner = userRepository.findById(assignedBy)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!assigner.getRole().equals(User.Role.STAFF) && !assigner.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Only staff or admin can assign complaints");
        }
        
        complaint.setAssignedDepartment(department);
        complaint.setStatus(Complaint.Status.ASSIGNED);
        
        Complaint savedComplaint = complaintRepository.save(complaint);
        
        // Add assignment comment
        Comment assignmentComment = new Comment(
                String.format("Complaint assigned to %s department", department.getName()), 
                savedComplaint, assigner, Comment.CommentType.ASSIGNMENT);
        assignmentComment.setIsInternal(true);
        commentRepository.save(assignmentComment);
        
        return savedComplaint;
    }
    
    // Assign complaint to staff member
    public Complaint assignComplaintToStaff(Long complaintId, Long staffId, Long assignedBy) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        User staff = userRepository.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Staff member not found"));
        
        User assigner = userRepository.findById(assignedBy)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!staff.getRole().equals(User.Role.STAFF) && !staff.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Can only assign to staff or admin users");
        }
        
        complaint.setAssignedStaff(staff);
        complaint.setStatus(Complaint.Status.IN_PROGRESS);
        
        Complaint savedComplaint = complaintRepository.save(complaint);
        
        // Add assignment comment
        Comment assignmentComment = new Comment(
                String.format("Complaint assigned to %s", staff.getFullName()), 
                savedComplaint, assigner, Comment.CommentType.ASSIGNMENT);
        assignmentComment.setIsInternal(true);
        commentRepository.save(assignmentComment);
        
        return savedComplaint;
    }
    
    // Update complaint status
    public Complaint updateComplaintStatus(Long complaintId, Complaint.Status newStatus, Long updatedBy, String reason) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        User updater = userRepository.findById(updatedBy)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Complaint.Status oldStatus = complaint.getStatus();
        complaint.setStatus(newStatus);
        
        Complaint savedComplaint = complaintRepository.save(complaint);
        
        // Add status update comment
        String commentText = String.format("Status changed from %s to %s", 
                oldStatus.getDisplayName(), newStatus.getDisplayName());
        if (reason != null && !reason.trim().isEmpty()) {
            commentText += ". Reason: " + reason;
        }
        
        Comment statusComment = new Comment(commentText, savedComplaint, updater, Comment.CommentType.STATUS_UPDATE);
        statusComment.setIsInternal(false);
        commentRepository.save(statusComment);
        
        return savedComplaint;
    }
    
    // Update complaint priority
    public Complaint updateComplaintPriority(Long complaintId, Complaint.Priority newPriority, Long updatedBy) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        User updater = userRepository.findById(updatedBy)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!updater.getRole().equals(User.Role.STAFF) && !updater.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Only staff or admin can update priority");
        }
        
        Complaint.Priority oldPriority = complaint.getPriority();
        complaint.setPriority(newPriority);
        
        Complaint savedComplaint = complaintRepository.save(complaint);
        
        // Add priority update comment
        Comment priorityComment = new Comment(
                String.format("Priority changed from %s to %s", oldPriority.getDisplayName(), newPriority.getDisplayName()), 
                savedComplaint, updater, Comment.CommentType.STATUS_UPDATE);
        priorityComment.setIsInternal(true);
        commentRepository.save(priorityComment);
        
        return savedComplaint;
    }
    
    // Get complaints by department
    public Page<Complaint> getComplaintsByDepartment(Long departmentId, Pageable pageable) {
        return complaintRepository.findByAssignedDepartmentId(departmentId, pageable);
    }
    
    // Get complaints by status
    public Page<Complaint> getComplaintsByStatus(Complaint.Status status, Pageable pageable) {
        return complaintRepository.findByStatus(status, pageable);
    }
    
    // Get complaints by category
    public Page<Complaint> getComplaintsByCategory(Complaint.Category category, Pageable pageable) {
        return complaintRepository.findByCategory(category, pageable);
    }
    
    // Search complaints
    public Page<Complaint> searchComplaints(String keyword, Pageable pageable) {
        return complaintRepository.searchComplaints(keyword, pageable);
    }
    
    // Get complaints assigned to staff
    public Page<Complaint> getComplaintsAssignedToStaff(Long staffId, Pageable pageable) {
        return complaintRepository.findByAssignedStaffId(staffId, pageable);
    }
    
    // Get dashboard statistics
    public ComplaintDashboardStats getDashboardStats() {
        Long totalComplaints = complaintRepository.count();
        Long newComplaints = complaintRepository.countByStatus(Complaint.Status.SUBMITTED);
        Long inProgressComplaints = complaintRepository.countByStatus(Complaint.Status.IN_PROGRESS);
        Long resolvedComplaints = complaintRepository.countByStatus(Complaint.Status.RESOLVED);
        List<Complaint> urgentComplaints = complaintRepository.findUrgentComplaints();
        
        return new ComplaintDashboardStats(totalComplaints, newComplaints, inProgressComplaints, 
                resolvedComplaints, (long) urgentComplaints.size());
    }
    
    // Get recent complaints
    public Page<Complaint> getRecentComplaints(Pageable pageable) {
        return complaintRepository.findRecentComplaints(pageable);
    }
    
    // Get complaints in date range
    public List<Complaint> getComplaintsInDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return complaintRepository.findByDateRange(startDate, endDate);
    }
    
    // Resolve complaint
    public Complaint resolveComplaint(Long complaintId, Long resolvedBy, String resolutionNote) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        
        User resolver = userRepository.findById(resolvedBy)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        complaint.setStatus(Complaint.Status.RESOLVED);
        complaint.setResolvedAt(LocalDateTime.now());
        
        Complaint savedComplaint = complaintRepository.save(complaint);
        
        // Add resolution comment
        String commentText = "Complaint resolved.";
        if (resolutionNote != null && !resolutionNote.trim().isEmpty()) {
            commentText += " Resolution: " + resolutionNote;
        }
        
        Comment resolutionComment = new Comment(commentText, savedComplaint, resolver, Comment.CommentType.RESOLUTION);
        resolutionComment.setIsInternal(false);
        commentRepository.save(resolutionComment);
        
        return savedComplaint;
    }
    
    // Check if user can view complaint
    public boolean canUserViewComplaint(Long complaintId, Long userId) {
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
        
        // Citizens can only view their own complaints
        if (user.getRole().equals(User.Role.CITIZEN)) {
            return complaint.getCitizen().getId().equals(userId);
        }
        
        // Staff and Admin can view all complaints
        return user.getRole().equals(User.Role.STAFF) || user.getRole().equals(User.Role.ADMIN);
    }
    
    // Inner class for dashboard statistics
    public static class ComplaintDashboardStats {
        private Long totalComplaints;
        private Long newComplaints;
        private Long inProgressComplaints;
        private Long resolvedComplaints;
        private Long urgentComplaints;
        
        public ComplaintDashboardStats(Long totalComplaints, Long newComplaints, Long inProgressComplaints, 
                                     Long resolvedComplaints, Long urgentComplaints) {
            this.totalComplaints = totalComplaints;
            this.newComplaints = newComplaints;
            this.inProgressComplaints = inProgressComplaints;
            this.resolvedComplaints = resolvedComplaints;
            this.urgentComplaints = urgentComplaints;
        }
        
        // Getters
        public Long getTotalComplaints() { return totalComplaints; }
        public Long getNewComplaints() { return newComplaints; }
        public Long getInProgressComplaints() { return inProgressComplaints; }
        public Long getResolvedComplaints() { return resolvedComplaints; }
        public Long getUrgentComplaints() { return urgentComplaints; }
    }
}