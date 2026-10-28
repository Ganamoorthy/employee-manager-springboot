package com.example.employee_manager.repository;

import com.example.employee_manager.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // Used to check for duplicate emails
    boolean existsByEmail(String email);

    // Used to check for duplicate emails when editing (ignoring the current user's
    // ID)
    boolean existsByEmailAndIdNot(String email, Long id);

    // Task 11 Challenge: Search by name
    List<Employee> findByFullNameContainingIgnoreCase(String keyword);
}