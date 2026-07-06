package com.tdd.tddmock.usermanager;

import java.util.Objects;

public class User {
    public String username;
    public String email;

    public User(String username, String email) {
        this.username = username;
        this.email = email;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        User otherUser = (User) obj;
        return Objects.equals(username, otherUser.username) &&
               Objects.equals(email, otherUser.email);
    }

    // Getters and setters (omitted for brevity)
}
