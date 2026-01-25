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
import com.gzouli.ERP.exception.BusinessException;
import com.gzouli.ERP.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService{
    private final CognitoService cognitoService;
    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeSummaryDTO createEmployee(EmployeeRegistrationDTO dto) {
        // 1. Validations Métiers
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new BusinessException("Cet email est déjà utilisé par un autre employé.");
        }
        if (employeeRepository.existsByIdCardNumber(dto.getIdCardNumber())) {
            throw new BusinessException("Ce numéro de CIN existe déjà.");
        }

        // 2. Appel AWS Cognito (Récupération de l'UUID)
        String cognitoSub = cognitoService.createCognitoUser(
                dto.getEmail(),
                dto.getFirstName(),
                dto.getLastName(),
                dto.getRole().name()
        );

        // 3. Sauvegarde Locale
        Employee employee = getEmployee(dto, cognitoSub);

        Employee saved = employeeRepository.save(employee);

        return mapToSummaryDTO(saved);
    }

    private static Employee getEmployee(EmployeeRegistrationDTO dto, String cognitoSub) {
        Employee employee = new Employee();
        employee.setCognitoId(cognitoSub);
        employee.setEmail(dto.getEmail());
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setIdCardNumber(dto.getIdCardNumber());
        employee.setAddress(dto.getAddress());
        employee.setSalary(dto.getSalary());
        employee.setBirthday(dto.getBirthday());
        employee.setRole(dto.getRole());
        employee.setActive(true);
        return employee;
    }

    @Override
    public EmployeeSummaryDTO updateEmployeeDetails(Long id, EmployeeSummaryDTO dto) {
        // 1. Récupération
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'ID : " + id));

        // 2. Mise à jour des champs autorisés (Pas l'email/cognitoId)
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setPhoneNumber(dto.getPhoneNumber());
        employee.setAddress(dto.getAddress());
        employee.setSalary(dto.getSalary());
        employee.setBirthday(dto.getBirthday());

        // Si le rôle change, c'est complexe avec Cognito (supprimer de l'ancien groupe, ajouter au nouveau).
        // Pour ce MVP, on suppose que le rôle ne change pas via ce formulaire simple, sinon il faut appeler CognitoService.

        Employee updated = employeeRepository.save(employee);
        return mapToSummaryDTO(updated);
    }

    @Override
    public void toggleEmployeeStatus(Long id, boolean isActive) {
        // 1. Récupération
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable avec l'ID : " + id));

        // 2. Logique AWS
        if (isActive) {
            cognitoService.enableUser(employee.getEmail()); // ou getCognitoId selon config
        } else {
            cognitoService.disableUser(employee.getEmail());
        }

        // 3. Mise à jour BDD Locale
        employee.setActive(isActive);
        employeeRepository.save(employee);
    }

    @Override
    public List<EmployeeSummaryDTO> getAllEmployees() {
        return employeeRepository.findAll().stream()
                .map(this::mapToSummaryDTO)
                .toList();
    }

    @Override
    public EmployeeDetailDTO getEmployeeById(Long id) {
        Employee emp = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employé introuvable"));

        // Mapping manuel ou via Mapper
        EmployeeDetailDTO dto = new EmployeeDetailDTO();
        dto.setId(emp.getId());
        dto.setFirstName(emp.getFirstName());
        dto.setLastName(emp.getLastName());
        dto.setEmail(emp.getEmail());
        dto.setSalary(emp.getSalary());
        dto.setRole(emp.getRole());
        dto.setActive(emp.isActive());
        dto.setIdCardNumber(emp.getIdCardNumber());
        dto.setPhoneNumber(emp.getPhoneNumber());
        dto.setAddress(emp.getAddress());
        dto.setBirthday(emp.getBirthday());
        // dto.setCurrentCar... (logique véhicule à part)

        return dto;
    }

    // Helper de mapping simple
    private EmployeeSummaryDTO mapToSummaryDTO(Employee e) {
        EmployeeSummaryDTO dto = new EmployeeSummaryDTO();
        dto.setId(e.getId());
        dto.setFirstName(e.getFirstName());
        dto.setLastName(e.getLastName());
        dto.setEmail(e.getEmail());
        dto.setRole(e.getRole().name());
        dto.setActive(e.isActive());
        dto.setIdCardNumber(e.getIdCardNumber());
        dto.setSalary(e.getSalary());
        dto.setBirthday(e.getBirthday());
        dto.setAddress(e.getAddress());
        dto.setPhoneNumber(e.getPhoneNumber());
        return dto;
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
