package com.example.complaints.web;

import com.example.complaints.service.ComplaintService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/staff")
@PreAuthorize("hasAnyRole('STAFF','ADMIN')")
public class StaffMvcController {

    private final ComplaintService complaintService;

    public StaffMvcController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("unassigned", complaintService.listUnassignedComplaints());
        return "staff/dashboard";
    }

    @PostMapping("/assign")
    public String assign(@RequestParam Long complaintId, @RequestParam String department, Authentication authentication) {
        Long staffId = 200L; // placeholder
        complaintService.assignDepartment(complaintId, department, staffId);
        return "redirect:/staff/dashboard";
    }
}