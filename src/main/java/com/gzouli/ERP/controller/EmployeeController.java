package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.EmployeeDetailDTO;
import com.gzouli.ERP.dto.EmployeeRegistrationDTO;
import com.gzouli.ERP.dto.EmployeeSummaryDTO;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.service.CognitoService;
import com.gzouli.ERP.service.EmployeeService;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/employees")
public class EmployeeController {

    private final CognitoService cognitoService;
    private final EmployeeService employeeService;

    public EmployeeController(CognitoService cognitoService, EmployeeService employeeService) {
        this.cognitoService = cognitoService;
        this.employeeService = employeeService;
    }


    @PostMapping
    public ResponseEntity<EmployeeSummaryDTO> createEmployee(@RequestBody EmployeeRegistrationDTO dto) {
        Employee created = cognitoService.createEmployee(dto);
//        return ResponseEntity.ok(created);
        return null;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleEmployeeStatus(@PathVariable Long id, @RequestParam(name = "status") boolean status)
    {
        cognitoService.toggleEmployeeStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public List<EmployeeSummaryDTO> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @GetMapping("/{id}")
    public EmployeeDetailDTO getEmployee(@PathVariable Long id) {
        return employeeService.getEmployeeById(id);
    }

    @PutMapping("/{id}")
    public EmployeeDetailDTO updateEmployeeDetails(@PathVariable Long id, @RequestBody EmployeeDetailDTO employeeDetailDTO)
    {
        return employeeService.updateEmployeeDetails(id, employeeDetailDTO);
    }
}
