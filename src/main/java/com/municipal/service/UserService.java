package com.municipal.service;

import com.municipal.entity.Department;
import com.municipal.entity.User;
import com.municipal.repository.DepartmentRepository;
import com.municipal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    // Create new user
    public User createUser(String username, String email, String password, String fullName, 
                          User.Role role, String phoneNumber, Long departmentId) {
        // Check if username or email already exists
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        User user = new User(username, email, passwordEncoder.encode(password), fullName, role);
        user.setPhoneNumber(phoneNumber);
        
        // Assign department if provided and user is staff/admin
        if (departmentId != null && (role.equals(User.Role.STAFF) || role.equals(User.Role.ADMIN))) {
            Department department = departmentRepository.findById(departmentId)
                    .orElseThrow(() -> new RuntimeException("Department not found"));
            user.setDepartment(department);
        }
        
        return userRepository.save(user);
    }
    
    // Get user by ID
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    // Get user by username
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }
    
    // Get user by email
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    // Get all users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    // Get users by role
    public List<User> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role);
    }
    
    // Get active users by role
    public List<User> getActiveUsersByRole(User.Role role) {
        return userRepository.findByRoleAndIsActive(role, true);
    }
    
    // Get staff and admin users
    public List<User> getStaffAndAdminUsers() {
        return userRepository.findAllStaffAndAdmin();
    }
    
    // Get users by department and role
    public List<User> getUsersByDepartmentAndRole(Long departmentId, User.Role role) {
        return userRepository.findByDepartmentAndRole(departmentId, role);
    }
    
    // Update user profile
    public User updateUserProfile(Long userId, String fullName, String phoneNumber, String email) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if email is already taken by another user
        if (!user.getEmail().equals(email) && userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }
        
        user.setFullName(fullName);
        user.setPhoneNumber(phoneNumber);
        user.setEmail(email);
        
        return userRepository.save(user);
    }
    
    // Change password
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    // Reset password (admin only)
    public void resetPassword(Long userId, String newPassword, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Only admins can reset passwords");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
    
    // Activate/Deactivate user
    public User toggleUserStatus(Long userId, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Only admins can change user status");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setIsActive(!user.getIsActive());
        return userRepository.save(user);
    }
    
    // Assign user to department
    public User assignUserToDepartment(Long userId, Long departmentId, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Only admins can assign users to departments");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        user.setDepartment(department);
        return userRepository.save(user);
    }
    
    // Change user role
    public User changeUserRole(Long userId, User.Role newRole, Long adminId) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin not found"));
        
        if (!admin.getRole().equals(User.Role.ADMIN)) {
            throw new RuntimeException("Only admins can change user roles");
        }
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setRole(newRole);
        return userRepository.save(user);
    }
    
    // Check if username exists
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    // Check if email exists
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
}