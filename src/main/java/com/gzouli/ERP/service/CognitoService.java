package com.gzouli.ERP.service;

import com.gzouli.ERP.dto.EmployeeRegistrationDTO;
import com.gzouli.ERP.entity.Employee;

public interface CognitoService {
    String createCognitoUser(String email, String firstName, String lastName, String roleName);

    void disableUser(String username);

    void enableUser(String username);
}
