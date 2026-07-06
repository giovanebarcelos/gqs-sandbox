package com.tdd.tddmock.authentication;

public interface AuthenticationService {
    boolean authenticate(String username, String password);
    // Other methods related to authentication...
}
