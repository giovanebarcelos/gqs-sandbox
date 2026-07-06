package com.tdd.tddmock.authentication;

public class LoginManager {
    private AuthenticationService authService;

    public LoginManager(AuthenticationService authService) {
        this.authService = authService;
    }

    public String login(String username, String password) {
        if (authService.authenticate(username, password)) {
            return "Login successful";
        } else {
            return "Login failed";
        }
    }
}

