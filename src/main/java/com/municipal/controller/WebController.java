package com.municipal.controller;

import com.municipal.entity.Complaint;
import com.municipal.entity.Department;
import com.municipal.entity.User;
import com.municipal.security.UserPrincipal;
import com.municipal.service.ComplaintService;
import com.municipal.service.UserService;
import com.municipal.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
public class WebController {
    
    @Autowired
    private ComplaintService complaintService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    // Home page
    @GetMapping("/")
    public String home() {
        return "index";
    }
    
    // Login page
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }
    
    // Registration page
    @GetMapping("/register")
    public String register() {
        return "auth/register";
    }
    
    // Citizen complaint form
    @GetMapping("/complaints/new")
    public String newComplaintForm(Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null || !currentUser.getRole().equals(User.Role.CITIZEN)) {
            return "redirect:/login";
        }
        
        model.addAttribute("categories", Complaint.Category.values());
        return "citizen/new-complaint";
    }
    
    // Handle complaint submission
    @PostMapping("/complaints/submit")
    public String submitComplaint(
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam(value = "locationAddress", required = false) String locationAddress,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            @AuthenticationPrincipal UserPrincipal currentUser,
            RedirectAttributes redirectAttributes) {
        
        try {
            if (currentUser == null || !currentUser.getRole().equals(User.Role.CITIZEN)) {
                return "redirect:/login";
            }
            
            Complaint.Category complaintCategory = Complaint.Category.valueOf(category.toUpperCase());
            Complaint complaint = complaintService.createComplaint(
                    complaintCategory, description, locationAddress, currentUser.getId(), attachment);
            
            redirectAttributes.addFlashAttribute("successMessage", 
                    "Complaint submitted successfully! Your complaint ID is: " + complaint.getId());
            return "redirect:/complaints/track/" + complaint.getId();
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", 
                    "Failed to submit complaint: " + e.getMessage());
            return "redirect:/complaints/new";
        }
    }
    
    // Citizen complaints list
    @GetMapping("/complaints/my")
    public String myComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        if (currentUser == null || !currentUser.getRole().equals(User.Role.CITIZEN)) {
            return "redirect:/login";
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Complaint> complaints = complaintService.getComplaintsByCitizen(currentUser.getId(), pageable);
        
        model.addAttribute("complaints", complaints);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", complaints.getTotalPages());
        
        return "citizen/my-complaints";
    }
    
    // Track complaint status
    @GetMapping("/complaints/track/{id}")
    public String trackComplaint(@PathVariable Long id, Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        try {
            if (!complaintService.canUserViewComplaint(id, currentUser.getId())) {
                model.addAttribute("errorMessage", "Access denied");
                return "error/403";
            }
            
            Optional<Complaint> complaintOpt = complaintService.getComplaintById(id);
            if (!complaintOpt.isPresent()) {
                model.addAttribute("errorMessage", "Complaint not found");
                return "error/404";
            }
            
            Complaint complaint = complaintOpt.get();
            model.addAttribute("complaint", complaint);
            
            return "citizen/track-complaint";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/500";
        }
    }
    
    // Staff dashboard
    @GetMapping("/dashboard/staff")
    public String staffDashboard(Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null || 
            (!currentUser.getRole().equals(User.Role.STAFF) && !currentUser.getRole().equals(User.Role.ADMIN))) {
            return "redirect:/login";
        }
        
        // Get dashboard statistics
        ComplaintService.ComplaintDashboardStats stats = complaintService.getDashboardStats();
        model.addAttribute("stats", stats);
        
        // Get unassigned complaints
        List<Complaint> unassignedComplaints = complaintService.getUnassignedComplaints();
        model.addAttribute("unassignedComplaints", unassignedComplaints);
        
        // Get recent complaints
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Complaint> recentComplaints = complaintService.getRecentComplaints(pageable);
        model.addAttribute("recentComplaints", recentComplaints.getContent());
        
        return "staff/dashboard";
    }
    
    // Admin dashboard
    @GetMapping("/dashboard/admin")
    public String adminDashboard(Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null || !currentUser.getRole().equals(User.Role.ADMIN)) {
            return "redirect:/login";
        }
        
        // Get dashboard statistics
        ComplaintService.ComplaintDashboardStats stats = complaintService.getDashboardStats();
        model.addAttribute("stats", stats);
        
        // Get all departments
        List<Department> departments = departmentRepository.findAllActiveDepartments();
        model.addAttribute("departments", departments);
        
        // Get all staff users
        List<User> staffUsers = userService.getStaffAndAdminUsers();
        model.addAttribute("staffUsers", staffUsers);
        
        return "admin/dashboard";
    }
    
    // Staff complaint assignment page
    @GetMapping("/staff/complaints/assign")
    public String assignmentPage(Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null || 
            (!currentUser.getRole().equals(User.Role.STAFF) && !currentUser.getRole().equals(User.Role.ADMIN))) {
            return "redirect:/login";
        }
        
        // Get unassigned complaints
        List<Complaint> unassignedComplaints = complaintService.getUnassignedComplaints();
        model.addAttribute("unassignedComplaints", unassignedComplaints);
        
        // Get departments
        List<Department> departments = departmentRepository.findAllActiveDepartments();
        model.addAttribute("departments", departments);
        
        // Get staff users
        List<User> staffUsers = userService.getStaffAndAdminUsers();
        model.addAttribute("staffUsers", staffUsers);
        
        return "staff/assign-complaints";
    }
    
    // View all complaints (Staff/Admin)
    @GetMapping("/staff/complaints")
    public String viewAllComplaints(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String search,
            Model model,
            @AuthenticationPrincipal UserPrincipal currentUser) {
        
        if (currentUser == null || 
            (!currentUser.getRole().equals(User.Role.STAFF) && !currentUser.getRole().equals(User.Role.ADMIN))) {
            return "redirect:/login";
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Complaint> complaints;
        
        if (search != null && !search.trim().isEmpty()) {
            complaints = complaintService.searchComplaints(search, pageable);
        } else if (status != null && !status.isEmpty()) {
            try {
                Complaint.Status complaintStatus = Complaint.Status.valueOf(status.toUpperCase());
                complaints = complaintService.getComplaintsByStatus(complaintStatus, pageable);
            } catch (IllegalArgumentException e) {
                complaints = complaintService.getAllComplaints(pageable);
            }
        } else if (category != null && !category.isEmpty()) {
            try {
                Complaint.Category complaintCategory = Complaint.Category.valueOf(category.toUpperCase());
                complaints = complaintService.getComplaintsByCategory(complaintCategory, pageable);
            } catch (IllegalArgumentException e) {
                complaints = complaintService.getAllComplaints(pageable);
            }
        } else {
            complaints = complaintService.getAllComplaints(pageable);
        }
        
        model.addAttribute("complaints", complaints);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", complaints.getTotalPages());
        model.addAttribute("selectedStatus", status);
        model.addAttribute("selectedCategory", category);
        model.addAttribute("searchKeyword", search);
        model.addAttribute("statuses", Complaint.Status.values());
        model.addAttribute("categories", Complaint.Category.values());
        
        return "staff/view-complaints";
    }
    
    // View complaint details (Staff/Admin)
    @GetMapping("/staff/complaints/{id}")
    public String viewComplaintDetails(@PathVariable Long id, Model model, @AuthenticationPrincipal UserPrincipal currentUser) {
        if (currentUser == null || 
            (!currentUser.getRole().equals(User.Role.STAFF) && !currentUser.getRole().equals(User.Role.ADMIN))) {
            return "redirect:/login";
        }
        
        try {
            Optional<Complaint> complaintOpt = complaintService.getComplaintById(id);
            if (!complaintOpt.isPresent()) {
                model.addAttribute("errorMessage", "Complaint not found");
                return "error/404";
            }
            
            Complaint complaint = complaintOpt.get();
            model.addAttribute("complaint", complaint);
            
            // Get departments and staff for assignment
            List<Department> departments = departmentRepository.findAllActiveDepartments();
            model.addAttribute("departments", departments);
            
            List<User> staffUsers = userService.getStaffAndAdminUsers();
            model.addAttribute("staffUsers", staffUsers);
            
            model.addAttribute("statuses", Complaint.Status.values());
            model.addAttribute("priorities", Complaint.Priority.values());
            
            return "staff/complaint-details";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "error/500";
        }
    }
}