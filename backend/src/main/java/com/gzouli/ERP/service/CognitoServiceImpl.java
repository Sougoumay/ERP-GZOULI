package com.gzouli.ERP.service;

import com.gzouli.ERP.exception.CognitoInteractionException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient;
import software.amazon.awssdk.services.cognitoidentityprovider.model.*;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CognitoServiceImpl implements CognitoService {

    private static final String LOWERCASE = "abcdefghijklmnopqrstuvwxyz";
    private static final String UPPERCASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?";
    private static final int PASSWORD_LENGTH = 12;
    private static final SecureRandom random = new SecureRandom();

    private final CognitoIdentityProviderClient cognitoClient;


    @Value("${aws.cognito.userPoolId}")
    private String userPoolId;


    @Override
    public String createCognitoUser(String email, String firstName, String lastName, String roleName) {
        try {
            // 1. Création User
            String tempPassword = generateStrongPassword();

            AdminCreateUserRequest userRequest = AdminCreateUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(email)
                    .temporaryPassword(tempPassword)
                    .userAttributes(
                            AttributeType.builder().name("email").value(email).build(),
                            AttributeType.builder().name("email_verified").value("true").build(),
                            AttributeType.builder().name("given_name").value(firstName).build(),
                            AttributeType.builder().name("family_name").value(lastName).build()
                    )
                    .messageAction(MessageActionType.SUPPRESS)
                    .build();

            AdminCreateUserResponse response = cognitoClient.adminCreateUser(userRequest);
            String sub = response.user().username();

            // 2. Ajout au Groupe
            if (roleName != null) {
                AdminAddUserToGroupRequest groupRequest = AdminAddUserToGroupRequest.builder()
                        .userPoolId(userPoolId)
                        .username(email)
                        .groupName(roleName)
                        .build();
                cognitoClient.adminAddUserToGroup(groupRequest);
            }
            log.info("Utilisateur Cognito créé avec succès : {}", sub);
            return sub;

        } catch (CognitoIdentityProviderException e) {
            // On capture l'erreur AWS et on la relance proprement
            log.error("Échec de la création Cognito: {}", e.awsErrorDetails().errorMessage(), e);
            throw new CognitoInteractionException(e.awsErrorDetails().errorMessage(), e);
        }
    }

    @Override
    public void disableUser(String username) {
        try {
            AdminDisableUserRequest request = AdminDisableUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build();
            cognitoClient.adminDisableUser(request);
        } catch (CognitoIdentityProviderException e) {
            throw new CognitoInteractionException("Impossible de désactiver l'utilisateur AWS", e);
        }
    }

    @Override
    public void enableUser(String username) {
        try {
            AdminEnableUserRequest request = AdminEnableUserRequest.builder()
                    .userPoolId(userPoolId)
                    .username(username)
                    .build();
            cognitoClient.adminEnableUser(request);
        } catch (CognitoIdentityProviderException e) {
            throw new CognitoInteractionException("Impossible de réactiver l'utilisateur AWS", e);
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
