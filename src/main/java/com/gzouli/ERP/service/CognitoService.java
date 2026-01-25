package com.gzouli.ERP.service;

public interface CognitoService {
    String createCognitoUser(String email, String firstName, String lastName, String roleName);

    void disableUser(String username);

    void enableUser(String username);
}
