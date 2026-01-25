package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.EmployeeDetailDTO;
import com.gzouli.ERP.dto.EmployeeRegistrationDTO;
import com.gzouli.ERP.dto.EmployeeSummaryDTO;
import com.gzouli.ERP.dto.EmployeeUpdateDTO;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.service.CognitoService;
import com.gzouli.ERP.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@PreAuthorize("hasRole('ADMIN')")
@RestController
@RequestMapping("/api/admin/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

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
    @PutMapping("/{id}/status")
    public ResponseEntity<Void> toggleStatus(@PathVariable Long id, @RequestParam boolean active) {
        employeeService.toggleEmployeeStatus(id, active);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
}