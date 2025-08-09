package com.municipal.service;

import com.municipal.entity.Department;
import com.municipal.repository.DepartmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepartmentService {
    
    @Autowired
    private DepartmentRepository departmentRepository;
    
    // Create new department
    public Department createDepartment(String name, String description, String contactEmail, String contactPhone) {
        Department department = new Department(name, description);
        department.setContactEmail(contactEmail);
        department.setContactPhone(contactPhone);
        
        return departmentRepository.save(department);
    }
    
    // Get all departments
    public List<Department> getAllDepartments() {
        return departmentRepository.findAll();
    }
    
    // Get active departments
    public List<Department> getActiveDepartments() {
        return departmentRepository.findAllActiveDepartments();
    }
    
    // Get department by ID
    public Optional<Department> getDepartmentById(Long id) {
        return departmentRepository.findById(id);
    }
    
    // Get department by name
    public Optional<Department> getDepartmentByName(String name) {
        return departmentRepository.findByName(name);
    }
    
    // Update department
    public Department updateDepartment(Long id, String name, String description, String contactEmail, String contactPhone) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        department.setName(name);
        department.setDescription(description);
        department.setContactEmail(contactEmail);
        department.setContactPhone(contactPhone);
        
        return departmentRepository.save(department);
    }
    
    // Toggle department status
    public Department toggleDepartmentStatus(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Department not found"));
        
        department.setIsActive(!department.getIsActive());
        return departmentRepository.save(department);
    }
    
    // Check if department name exists
    public boolean departmentNameExists(String name) {
        return departmentRepository.existsByName(name);
    }
}