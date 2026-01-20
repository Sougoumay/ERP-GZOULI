package com.gzouli.ERP.controller;

import com.gzouli.ERP.dto.EmployeeRegistrationDTO;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.service.CognitoService;
import com.gzouli.ERP.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/employees")
public class EmployeeController {

    private final CognitoService cognitoService;

    public EmployeeController(CognitoService cognitoService) {
        this.cognitoService = cognitoService;
    }

    @GetMapping(name = "")
    public String greetings() {
        return "Hello Employee X";
    }

    @PostMapping
    public ResponseEntity<Employee> createEmployee(@RequestBody EmployeeRegistrationDTO dto) {
        Employee created = cognitoService.createEmployee(dto);
        return ResponseEntity.ok(created);
    }
}
