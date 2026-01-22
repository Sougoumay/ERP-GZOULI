package com.gzouli.ERP.dao;

import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    boolean existsByEmail(String email);

    // CRUCIAL : Pour retrouver l'employé connecté via son Token JWT (sub)
    Optional<Employee> findByCognitoId(String cognitoId);

    // Pour filtrer par rôle (ex: lister que les techniciens pour un projet)
    List<Employee> findByRole(Role role);

    Optional<Employee> getEmployeesById(Long id);

    Optional<Employee> getEmployeesByCognitoId(String cognitoId);

    Optional<Employee> getEmployeesByRole(Role role);

//    List<Employee> findAll();






}
