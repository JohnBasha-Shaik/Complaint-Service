package com.example.complaints.web;

import com.example.complaints.model.Complaint;
import com.example.complaints.model.ComplaintComment;
import com.example.complaints.service.CommentService;
import com.example.complaints.service.ComplaintService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@Validated
public class ComplaintRestController {

    private final ComplaintService complaintService;
    private final CommentService commentService;

    public ComplaintRestController(ComplaintService complaintService, CommentService commentService) {
        this.complaintService = complaintService;
        this.commentService = commentService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CITIZEN')")
    @ResponseStatus(HttpStatus.CREATED)
    public Complaint createComplaint(
            @RequestParam("category") String category,
            @RequestParam("description") String description,
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
        return complaintService.createComplaint(complaint);
    }

    @GetMapping
    @PreAuthorize("hasRole('CITIZEN')")
    public List<Complaint> myComplaints(Authentication authentication) {
        Long citizenId = resolveUserId(authentication.getName());
        return complaintService.listCitizenComplaints(citizenId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CITIZEN','STAFF','ADMIN')")
    public Complaint getComplaint(@PathVariable Long id, Authentication authentication) {
        Complaint complaint = complaintService.getComplaint(id).orElseThrow();
        String username = authentication.getName();
        boolean isCitizen = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CITIZEN"));
        if (isCitizen && !complaint.getCitizenId().equals(resolveUserId(username))) {
            throw new RuntimeException("Forbidden");
        }
        return complaint;
    }

    @PostMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('CITIZEN','STAFF','ADMIN')")
    public ComplaintComment addComment(@PathVariable Long id, @RequestParam("message") String message, Authentication authentication) {
        Complaint complaint = complaintService.getComplaint(id).orElseThrow();
        String role = authentication.getAuthorities().stream().findFirst().map(a -> a.getAuthority().replace("ROLE_", "")).orElse("CITIZEN");
        Long userId = resolveUserId(authentication.getName());
        if ("CITIZEN".equals(role) && !complaint.getCitizenId().equals(userId)) {
            throw new RuntimeException("Forbidden");
        }
        return commentService.addComment(id, userId, role, message);
    }

    @GetMapping("/{id}/comments")
    @PreAuthorize("hasAnyRole('CITIZEN','STAFF','ADMIN')")
    public List<ComplaintComment> listComments(@PathVariable Long id, Authentication authentication) {
        Complaint complaint = complaintService.getComplaint(id).orElseThrow();
        boolean isCitizen = authentication.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CITIZEN"));
        if (isCitizen && !complaint.getCitizenId().equals(resolveUserId(authentication.getName()))) {
            throw new RuntimeException("Forbidden");
        }
        return commentService.listComments(id);
    }

    @GetMapping("/unassigned")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public List<Complaint> unassigned() {
        return complaintService.listUnassignedComplaints();
    }

    @PostMapping("/{id}/assign")
    @PreAuthorize("hasAnyRole('STAFF','ADMIN')")
    public Complaint assign(@PathVariable Long id, @RequestParam("department") String department, Authentication authentication) {
        Long staffId = resolveUserId(authentication.getName());
        return complaintService.assignDepartment(id, department, staffId);
    }

    private Long resolveUserId(String username) {
        if (username.startsWith("citizen")) return 100L;
        if (username.startsWith("staff")) return 200L;
        if (username.startsWith("admin")) return 1L;
        return 0L;
    }
}