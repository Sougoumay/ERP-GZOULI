package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.employee.EmployeeDetailDTO;
import com.gzouli.ERP.dto.employee.EmployeeRegistrationDTO;
import com.gzouli.ERP.dto.employee.EmployeeSummaryDTO;
import com.gzouli.ERP.dto.employee.SalaryAdvanceDTO;
import com.gzouli.ERP.service.EmployeeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/employees")
//@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<EmployeeSummaryDTO> createEmployee(@Valid @RequestBody EmployeeRegistrationDTO dto) {
        EmployeeSummaryDTO created = employeeService.createEmployee(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<EmployeeSummaryDTO>> getAllEmployees() {
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDetailDTO> getEmployee(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmployeeSummaryDTO> updateEmployee(@PathVariable Long id,
                                                             @Valid @RequestBody EmployeeSummaryDTO dto) {
        return ResponseEntity.ok(employeeService.updateEmployeeDetails(id, dto));
    }

    /**
     * Endpoint pour le "Soft Delete" et la Réactivation
     */
    @PatchMapping("/{id}")
    public ResponseEntity<Void> toggleStatus(@PathVariable Long id, @RequestParam(name = "status") boolean active) {
        // Appel au service (qui gère Cognito + BDD)
        employeeService.toggleEmployeeStatus(id, active);
        return ResponseEntity.noContent().build(); // 204 No Content
    }

    @PostMapping("/{id}/advances")
    public ResponseEntity<?> addAdvance(@PathVariable Long id, @RequestBody SalaryAdvanceDTO dto) {
        try {
            return ResponseEntity.ok(employeeService.addSalaryAdvance(id, dto));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/advances")
    public ResponseEntity<List<SalaryAdvanceDTO>> getAdvances(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.getEmployeeAdvances(id));
    }
}