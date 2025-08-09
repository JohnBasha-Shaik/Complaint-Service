package com.municipal.controller;

import com.municipal.entity.Complaint;
import com.municipal.entity.User;
import com.municipal.security.UserPrincipal;
import com.municipal.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
@RequestMapping("/api/complaints")
public class ComplaintController {
    
    @Autowired
    private ComplaintService complaintService;
    
    // Submit new complaint (Citizens only)
    @PostMapping("/submit")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<?> submitComplaint(
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam(value = "locationAddress", required = false) String locationAddress,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            Complaint.Category complaintCategory = Complaint.Category.valueOf(category.toUpperCase());
            
            Complaint complaint = complaintService.createComplaint(
                    complaintCategory, description, locationAddress, currentUser.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Complaint submitted successfully");
            response.put("complaintId", complaint.getId());
            response.put("status", complaint.getStatus());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to submit complaint: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get complaints for current user (Citizens see their own, Staff/Admin see all)
    @GetMapping("/my")
    @PreAuthorize("hasRole('CITIZEN')")
    public ResponseEntity<Page<Complaint>> getMyComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Complaint> complaints = complaintService.getComplaintsByCitizen(currentUser.getId(), pageable);
        return ResponseEntity.ok(complaints);
    }
    
    // Get complaint by ID
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('CITIZEN') or hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> getComplaint(@PathVariable Long id, @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            if (!complaintService.canUserViewComplaint(id, currentUser.getId())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Access denied");
                return ResponseEntity.status(403).body(error);
            }
            
            Optional<Complaint> complaint = complaintService.getComplaintById(id);
            if (complaint.isPresent()) {
                return ResponseEntity.ok(complaint.get());
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Complaint not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Get all complaints (Staff/Admin only)
    @GetMapping("/all")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Page<Complaint>> getAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<Complaint> complaints = complaintService.getAllComplaints(pageable);
        return ResponseEntity.ok(complaints);
    }
    
    // Get unassigned complaints
    @GetMapping("/unassigned")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<List<Complaint>> getUnassignedComplaints() {
        List<Complaint> complaints = complaintService.getUnassignedComplaints();
        return ResponseEntity.ok(complaints);
    }
    
    // Assign complaint to department
    @PostMapping("/{id}/assign/department")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> assignComplaintToDepartment(
            @PathVariable Long id,
            @RequestBody AssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            Complaint complaint = complaintService.assignComplaintToDepartment(
                    id, request.getDepartmentId(), currentUser.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Complaint assigned to department successfully");
            response.put("complaint", complaint);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Assign complaint to staff
    @PostMapping("/{id}/assign/staff")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> assignComplaintToStaff(
            @PathVariable Long id,
            @RequestBody AssignmentRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            Complaint complaint = complaintService.assignComplaintToStaff(
                    id, request.getStaffId(), currentUser.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Complaint assigned to staff successfully");
            response.put("complaint", complaint);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Update complaint status
    @PutMapping("/{id}/status")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> updateComplaintStatus(
            @PathVariable Long id,
            @RequestBody StatusUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            Complaint.Status status = Complaint.Status.valueOf(request.getStatus().toUpperCase());
            Complaint complaint = complaintService.updateComplaintStatus(
                    id, status, currentUser.getId(), request.getReason());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Status updated successfully");
            response.put("complaint", complaint);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Update complaint priority
    @PutMapping("/{id}/priority")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> updateComplaintPriority(
            @PathVariable Long id,
            @RequestBody PriorityUpdateRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            Complaint.Priority priority = Complaint.Priority.valueOf(request.getPriority().toUpperCase());
            Complaint complaint = complaintService.updateComplaintPriority(
                    id, priority, currentUser.getId());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Priority updated successfully");
            response.put("complaint", complaint);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Resolve complaint
    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<?> resolveComplaint(
            @PathVariable Long id,
            @RequestBody ResolutionRequest request,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        try {
            Complaint complaint = complaintService.resolveComplaint(
                    id, currentUser.getId(), request.getResolutionNote());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Complaint resolved successfully");
            response.put("complaint", complaint);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    // Search complaints
    @GetMapping("/search")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Page<Complaint>> searchComplaints(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Complaint> complaints = complaintService.searchComplaints(keyword, pageable);
        return ResponseEntity.ok(complaints);
    }
    
    // Get complaints by status
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Page<Complaint>> getComplaintsByStatus(
            @PathVariable String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Complaint.Status complaintStatus = Complaint.Status.valueOf(status.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Complaint> complaints = complaintService.getComplaintsByStatus(complaintStatus, pageable);
            return ResponseEntity.ok(complaints);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get complaints by category
    @GetMapping("/category/{category}")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<Page<Complaint>> getComplaintsByCategory(
            @PathVariable String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        try {
            Complaint.Category complaintCategory = Complaint.Category.valueOf(category.toUpperCase());
            Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
            Page<Complaint> complaints = complaintService.getComplaintsByCategory(complaintCategory, pageable);
            return ResponseEntity.ok(complaints);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Get dashboard statistics
    @GetMapping("/dashboard/stats")
    @PreAuthorize("hasRole('STAFF') or hasRole('ADMIN')")
    public ResponseEntity<ComplaintService.ComplaintDashboardStats> getDashboardStats() {
        ComplaintService.ComplaintDashboardStats stats = complaintService.getDashboardStats();
        return ResponseEntity.ok(stats);
    }
    
    // Request DTOs
    public static class AssignmentRequest {
        private Long departmentId;
        private Long staffId;
        
        public Long getDepartmentId() { return departmentId; }
        public void setDepartmentId(Long departmentId) { this.departmentId = departmentId; }
        public Long getStaffId() { return staffId; }
        public void setStaffId(Long staffId) { this.staffId = staffId; }
    }
    
    public static class StatusUpdateRequest {
        private String status;
        private String reason;
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
    
    public static class PriorityUpdateRequest {
        private String priority;
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }
    
    public static class ResolutionRequest {
        private String resolutionNote;
        
        public String getResolutionNote() { return resolutionNote; }
        public void setResolutionNote(String resolutionNote) { this.resolutionNote = resolutionNote; }
    }
}