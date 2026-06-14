package com.gzouli.ERP.service;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dto.employee.EmployeeRegistrationDTO;
import com.gzouli.ERP.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
@RequiredArgsConstructor
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    private final EmployeeService employeeService;

    @Override
    public void run(String... args) {
        String adminEmail = "sougoumay.hamid@gmail.com";

        // On vérifie si l'admin existe déjà pour ne pas le recréer à chaque redémarrage
        if (!employeeService.isEmailExist(adminEmail)) {
           try {
               System.out.println("Création automatique de l'Administrateur Principal...");

               EmployeeRegistrationDTO adminDto = getEmployeeRegistrationDTO(adminEmail);
               employeeService.createEmployee(adminDto);

               System.out.println("Admin créé avec succès. Un email d'invitation a été envoyé à : " + adminEmail);
           } catch (Exception e) {
               System.err.println("Échec de la création de l'admin : " + e.getMessage());
           }
        }
    }

    private static EmployeeRegistrationDTO getEmployeeRegistrationDTO(String adminEmail) {
        EmployeeRegistrationDTO adminDto = new EmployeeRegistrationDTO();
        adminDto.setEmail(adminEmail);
        adminDto.setFirstName("Sougouma");
        adminDto.setLastName("HAMID");
        adminDto.setRole(Role.ADMIN);

        // Données métiers par défaut (obligatoires selon votre Entité)
        adminDto.setSalary(0.0);
        adminDto.setPhoneNumber("0000000000");
        adminDto.setIdCardNumber("ADMIN001");
        adminDto.setAddress("Siège GZouli");
        return adminDto;
    }
}
