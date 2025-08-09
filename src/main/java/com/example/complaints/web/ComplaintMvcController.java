package com.example.complaints.web;

import com.example.complaints.model.Complaint;
import com.example.complaints.service.CommentService;
import com.example.complaints.service.ComplaintService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/complaints")
public class ComplaintMvcController {

    private final ComplaintService complaintService;
    private final CommentService commentService;

    public ComplaintMvcController(ComplaintService complaintService, CommentService commentService) {
        this.complaintService = complaintService;
        this.commentService = commentService;
    }

    @GetMapping("/new")
    @PreAuthorize("hasRole('CITIZEN')")
    public String newForm() {
        return "complaints/new";
    }

    @PostMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public String submit(
            @RequestParam("category") @NotBlank String category,
            @RequestParam("description") @NotBlank String description,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment,
            Authentication authentication
    ) throws IOException {
        Long citizenId = resolveUserId(authentication.getName());
        Complaint complaint = new Complaint();
        complaint.setCategory(category);
        complaint.setDescription(description);
        complaint.setCitizenId(citizenId);
        if (attachment != null && !attachment.isEmpty()) {
            Path uploadDir = Paths.get("uploads");
            Files.createDirectories(uploadDir);
            String filename = System.currentTimeMillis() + "_" + attachment.getOriginalFilename();
            Path dest = uploadDir.resolve(filename);
            Files.copy(attachment.getInputStream(), dest);
            complaint.setAttachment(dest.toString());
        }
        Complaint saved = complaintService.createComplaint(complaint);
        return "redirect:/complaints/" + saved.getId();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CITIZEN','STAFF','ADMIN')")
    public String view(@PathVariable Long id, Authentication authentication, Model model) {
        Complaint complaint = complaintService.getComplaint(id).orElseThrow();
        boolean isCitizen = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CITIZEN"));
        if (isCitizen && !complaint.getCitizenId().equals(resolveUserId(authentication.getName()))) {
            throw new RuntimeException("Forbidden");
        }
        model.addAttribute("complaint", complaint);
        model.addAttribute("comments", commentService.listComments(id));
        model.addAttribute("canAssign", authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_STAFF") || a.getAuthority().equals("ROLE_ADMIN")));
        model.addAttribute("isCitizen", isCitizen);
        return "complaints/view";
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('CITIZEN','STAFF','ADMIN')")
    public String addComment(@PathVariable Long id, @RequestParam("message") String message, Authentication authentication) {
        Complaint complaint = complaintService.getComplaint(id).orElseThrow();
        boolean isCitizen = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CITIZEN"));
        Long userId = resolveUserId(authentication.getName());
        if (isCitizen && !complaint.getCitizenId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        String role = isCitizen ? "CITIZEN" : "STAFF";
        commentService.addComment(id, userId, role, message);
        return "redirect:/complaints/" + id;
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('CITIZEN')")
    public String myComplaints(Authentication authentication, Model model) {
        Long citizenId = resolveUserId(authentication.getName());
        model.addAttribute("complaints", complaintService.listCitizenComplaints(citizenId));
        return "complaints/mine";
    }

    private Long resolveUserId(String username) {
        if (username.startsWith("citizen")) return 100L;
        if (username.startsWith("staff")) return 200L;
        if (username.startsWith("admin")) return 1L;
        return 0L;
    }
}