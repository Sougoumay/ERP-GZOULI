package com.gzouli.ERP.service;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dto.EmployeeRegistrationDTO;
import com.gzouli.ERP.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

@Service
//@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{
    private final CognitoIdentityProviderClient cognitoClient;
    private final EmployeeRepository employeeRepository;

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    public EmployeeServiceImpl(CognitoIdentityProviderClient cognitoClient, EmployeeRepository employeeRepository) {
        this.cognitoClient = cognitoClient;
        this.employeeRepository = employeeRepository;
    }


}
