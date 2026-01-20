package com.gzouli.ERP.service;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dto.EmployeeRegistrationDTO;
import com.gzouli.ERP.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final EmployeeRepository employeeRepository;
    private final CognitoService cognitoService;

    @Override
    public void run(String... args) {
        String adminEmail = "sougoumay.hamid@gmail.com";

        // On vérifie si l'admin existe déjà pour ne pas le recréer à chaque redémarrage
        if (!employeeRepository.existsByEmail(adminEmail)) {
            System.out.println("Création automatique de l'Administrateur Principal...");

            EmployeeRegistrationDTO adminDto = getEmployeeRegistrationDTO(adminEmail);

            // Appel du service que nous avons codé ensemble :
            // Il va créer le user dans Cognito ET dans la BDD locale
            cognitoService.createEmployee(adminDto);

            System.out.println("Admin créé avec succès. Mot de passe temporaire : Gzouli@2024");
        }
    }

    private static EmployeeRegistrationDTO getEmployeeRegistrationDTO(String adminEmail) {
        EmployeeRegistrationDTO adminDto = new EmployeeRegistrationDTO();
        adminDto.setEmail(adminEmail);
        adminDto.setFirstName("Super");
        adminDto.setLastName("Admin");
        adminDto.setRole(Role.ADMIN);

        // Données métiers par défaut (obligatoires selon votre Entité)
        adminDto.setSalary(0.0);
        adminDto.setPhoneNumber("0000000000");
        adminDto.setIdCardNumber("ADMIN001");
        adminDto.setAddress("Siège GZouli");
        return adminDto;
    }
}
