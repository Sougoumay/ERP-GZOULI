package com.gzouli.ERP.service;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dto.EmployeeDetailDTO;
import com.gzouli.ERP.dto.EmployeeRegistrationDTO;
import com.gzouli.ERP.dto.EmployeeSummaryDTO;
import com.gzouli.ERP.dto.EmployeeUpdateDTO;
import com.gzouli.ERP.entity.Car;
import com.gzouli.ERP.entity.Employee;
import com.gzouli.ERP.entity.Project;
import com.gzouli.ERP.entity.SalaryAdvance;
import com.gzouli.ERP.enums.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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


    @Override
    public EmployeeSummaryDTO createEmployee(EmployeeRegistrationDTO registrationDTO) {
        return null;
    }

    @Override
    public EmployeeDetailDTO updateEmployeeDetails(Long id, EmployeeDetailDTO employeeDetailDTO) {
        return null;
    }

    @Override
    public void toggleEmployeeStatus(Long id, boolean isActive) {

    }

    @Override
    public EmployeeDetailDTO getEmployeeById(Long id) {
        return null;
    }

    @Override
    public EmployeeSummaryDTO getEmployeeByCognitoId(String cognitoId) {
        return null;
    }

    @Override
    public List<EmployeeSummaryDTO> getAllEmployees() {
        List<Employee> employees = employeeRepository.findAll();
        List<EmployeeSummaryDTO> employeeSummaryDTOS = new ArrayList<>();

        employees.forEach(employee -> {
            EmployeeSummaryDTO summaryDTO = new EmployeeSummaryDTO();
            summaryDTO.setId(employee.getId());
            summaryDTO.setRole(employee.getRole().name());
            summaryDTO.setStatus(employee.isActive());
            summaryDTO.setFirstName(employee.getFirstName());
            summaryDTO.setLastName(employee.getLastName());

            employeeSummaryDTOS.add(summaryDTO);
        });
        return employeeSummaryDTOS;
    }

    @Override
    public List<EmployeeSummaryDTO> getEmployeesByRole(Role role) {
        return List.of();
    }

    @Override
    public void updateBaseSalary(Long id, Double newSalary) {

    }

    @Override
    public SalaryAdvance addSalaryAdvance(Long employeeId, Double amount, LocalDate date, String notes) {
        return null;
    }

    @Override
    public List<SalaryAdvance> getSalaryHistory(Long employeeId) {
        return List.of();
    }

    @Override
    public void assignVehicleToEmployee(Long employeeId, Long carId, LocalDate startDate) {

    }

    @Override
    public void unassignVehicle(Long employeeId, LocalDate returnDate) {

    }

    @Override
    public Optional<Car> getCurrentVehicle(Long employeeId) {
        return Optional.empty();
    }

    @Override
    public void assignToProject(Long employeeId, Long projectId) {

    }

    @Override
    public void removeFromProject(Long employeeId, Long projectId) {

    }

    @Override
    public List<Project> getAssignedProjects(Long employeeId) {
        return List.of();
    }

    @Override
    public boolean hasSubmittedJournalForDate(Long employeeId, LocalDate date) {
        return false;
    }

    @Override
    public int countWorkingDaysInMonth(Long employeeId, int month, int year) {
        return 0;
    }
}
