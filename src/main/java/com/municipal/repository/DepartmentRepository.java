package com.municipal.repository;

import com.municipal.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    
    Optional<Department> findByName(String name);
    
    List<Department> findByIsActive(Boolean isActive);
    
    Boolean existsByName(String name);
    
    @Query("SELECT d FROM Department d WHERE d.isActive = true ORDER BY d.name")
    List<Department> findAllActiveDepartments();
}