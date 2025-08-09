package com.municipal.config;

import com.municipal.entity.Department;
import com.municipal.entity.User;
import com.municipal.repository.DepartmentRepository;
import com.municipal.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize departments
        initializeDepartments();
        
        // Initialize default users
        initializeDefaultUsers();
    }
    
    private void initializeDepartments() {
        if (departmentRepository.count() == 0) {
            // Create default departments
            departmentRepository.save(new Department("Public Works", "Handles road maintenance, infrastructure, and general public works"));
            departmentRepository.save(new Department("Water & Sanitation", "Manages water supply, sewage, and sanitation services"));
            departmentRepository.save(new Department("Electrical Services", "Handles electrical infrastructure and street lighting"));
            departmentRepository.save(new Department("Waste Management", "Manages garbage collection and waste disposal"));
            departmentRepository.save(new Department("Parks & Recreation", "Maintains parks, recreational facilities, and green spaces"));
            departmentRepository.save(new Department("Building & Planning", "Handles building permits, zoning, and urban planning"));
            departmentRepository.save(new Department("Transportation", "Manages public transportation and traffic systems"));
        }
    }
    
    private void initializeDefaultUsers() {
        // Create admin user
        if (!userRepository.existsByUsername("admin")) {
            User admin = new User("admin", "admin@municipal.gov", 
                    passwordEncoder.encode("admin123"), "System Administrator", User.Role.ADMIN);
            admin.setPhoneNumber("555-000-0001");
            userRepository.save(admin);
        }
        
        // Create staff user
        if (!userRepository.existsByUsername("staff")) {
            User staff = new User("staff", "staff@municipal.gov", 
                    passwordEncoder.encode("staff123"), "Staff Member", User.Role.STAFF);
            staff.setPhoneNumber("555-000-0002");
            // Assign to Public Works department
            departmentRepository.findByName("Public Works").ifPresent(staff::setDepartment);
            userRepository.save(staff);
        }
        
        // Create citizen user
        if (!userRepository.existsByUsername("citizen")) {
            User citizen = new User("citizen", "citizen@example.com", 
                    passwordEncoder.encode("citizen123"), "John Citizen", User.Role.CITIZEN);
            citizen.setPhoneNumber("555-000-0003");
            userRepository.save(citizen);
        }
    }
}