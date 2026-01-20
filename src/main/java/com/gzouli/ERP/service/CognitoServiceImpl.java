package com.gzouli.ERP.service;

import com.gzouli.ERP.dao.EmployeeRepository;
import com.gzouli.ERP.dto.EmployeeRegistrationDTO;
import com.gzouli.ERP.entity.Employee;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class CognitoServiceImpl implements CognitoService {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";

    private static final int PASSWORD_LENGTH = 12;

    private static final SecureRandom random = new SecureRandom();

    private final CognitoIdentityProviderClient cognitoClient;
    private final EmployeeRepository employeeRepository;

    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;

    public CognitoServiceImpl(CognitoIdentityProviderClient cognitoClient, EmployeeRepository employeeRepository) {
        this.cognitoClient = cognitoClient;
        this.employeeRepository = employeeRepository;
    }

    @Override
    @Transactional
    public Employee createEmployee(EmployeeRegistrationDTO dto) {
        // 1. Validation locale
        if (employeeRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Cet email existe déjà dans la base de données.");
        }

        String cognitoSub = "";

        try {
            String password = generateStrongPassword();
            System.out.println("Votre mot de passe sécurisé est : " + password);
            // 2. Création de l'utilisateur dans AWS Cognito
            // On envoie seulement email, given_name, family_name
            AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(dto.getEmail())
                    .temporaryPassword(password) // Mot de passe temporaire forcé
                    .userAttributes(
                            AttributeType.builder().name("email").value(dto.getEmail()).build(),
                            AttributeType.builder().name("email_verified").value("true").build(), // Auto-vérifié car créé par Admin
                            AttributeType.builder().name("given_name").value(dto.getFirstName()).build(),
                            AttributeType.builder().name("family_name").value(dto.getLastName()).build()
                    )
                    .messageAction(MessageActionType.SUPPRESS) // On n'envoie pas l'email par défaut d'AWS (optionnel)
                    .build();

            AdminCreateUserResponse response = cognitoClient.adminCreateUser(userRequest);
            cognitoSub = response.user().username(); // C'est l'UUID unique (sub)

            // 3. Ajout au Groupe (Role) dans Cognito
            // Cela permet au Front d'avoir le rôle dans le Token JWT
            if (dto.getRole() != null) {
                AdminAddUserToGroupRequest groupRequest = AdminAddUserToGroupRequest.builder()
                        .userPoolId(userPoolId)
                        .username(dto.getEmail())
                        .groupName(dto.getRole().name()) // ADMIN, INGENIEUR, ou TECHNICIEN
                        .build();
                cognitoClient.adminAddUserToGroup(groupRequest);
            }

        } catch (CognitoIdentityProviderException e) {
            throw new RuntimeException("Erreur lors de la création Cognito: " + e.awsErrorDetails().errorMessage());
        }

        // 4. Sauvegarde dans la Base de Données Locale (PostgreSQL)
        Employee newEmployee = new Employee();

        // Lien technique
        newEmployee.setCognitoId(cognitoSub);

        // Données dupliquées (pour affichage rapide sans appeler AWS)
        newEmployee.setEmail(dto.getEmail());
        newEmployee.setFirstName(dto.getFirstName());
        newEmployee.setLastName(dto.getLastName());

        // Données Métiers Pures (Seulement en BDD)
        newEmployee.setIdCardNumber(dto.getIdCardNumber());
        newEmployee.setPhoneNumber(dto.getPhoneNumber());
        newEmployee.setAddress(dto.getAddress());
        newEmployee.setSalary(dto.getSalary());
        newEmployee.setBirthday(dto.getBirthday());
        newEmployee.setRole(dto.getRole());
        newEmployee.setActive(true);

        return employeeRepository.save(newEmployee);
    }

    @Transactional
    public void toggleEmployeeStatus(Long id, boolean shouldBeActive) {
        // 1. Récupération dans la BDD Locale
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Employé introuvable"));

        // 2. Mise à jour Locale (Soft Delete)
        employee.setActive(shouldBeActive);
        employeeRepository.save(employee);


        // 3. Mise à jour Cognito (Sécurité Bloquante)
        if (!shouldBeActive) {
            // DÉSACTIVATION : Empêche la connexion immédiate
            AdminDisableUserRequest disableRequest = AdminDisableUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(employee.getEmail()) // ou employee.getCognitoId() selon votre config AWS
                    .build();
            cognitoClient.adminDisableUser(disableRequest);
        } else {
            // RÉACTIVATION (au cas où il revient)
            AdminEnableUserRequest enableRequest = AdminEnableUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(employee.getEmail())
                    .build();
            cognitoClient.adminEnableUser(enableRequest);
        }
    }

    public static String generateStrongPassword() {
        List<Character> passwordChars = new ArrayList<>();

        // 1️⃣ Garantir au moins un caractère de chaque type
        passwordChars.add(randomChar(LOWERCASE));
        passwordChars.add(randomChar(UPPERCASE));
        passwordChars.add(randomChar(DIGITS));
        passwordChars.add(randomChar(SPECIAL));

        // 2️⃣ Compléter le reste avec un mix global
        String allChars = LOWERCASE + UPPERCASE + DIGITS + SPECIAL;
        for (int i = passwordChars.size(); i < PASSWORD_LENGTH; i++) {
            passwordChars.add(randomChar(allChars));
        }

        // 3️⃣ Mélanger pour éviter un ordre prévisible
        Collections.shuffle(passwordChars, random);

        // 4️⃣ Construire le mot de passe final
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        return password.toString();
    }

    private static char randomChar(String chars) {
        return chars.charAt(random.nextInt(chars.length()));
    }
}
