package com.gzouli.ERP.service;

import com.gzouli.ERP.dto.EmployeeRegistrationDTO;
import com.gzouli.ERP.entity.Employee;

public interface CognitoService {
    Employee createEmployee(EmployeeRegistrationDTO dto);
    void toggleEmployeeStatus(Long id, boolean shouldBeActive);
}
