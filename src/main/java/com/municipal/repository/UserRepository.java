package com.municipal.repository;

import com.municipal.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    Boolean existsByUsername(String username);
    
    Boolean existsByEmail(String email);
    
    List<User> findByRole(User.Role role);
    
    List<User> findByRoleAndIsActive(User.Role role, Boolean isActive);
    
    @Query("SELECT u FROM User u WHERE u.department.id = :departmentId AND u.role = :role")
    List<User> findByDepartmentAndRole(@Param("departmentId") Long departmentId, @Param("role") User.Role role);
    
    @Query("SELECT u FROM User u WHERE u.role IN ('STAFF', 'ADMIN') AND u.isActive = true")
    List<User> findAllStaffAndAdmin();
}