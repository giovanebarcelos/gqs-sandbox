package com.tdd.tddmock.usermanager;

public class UserManager {

    private UserRepository userRepository;

    public UserManager(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String username, String email) {
        // Check if user already exists
        if (userRepository.exists(username)) {
            throw new IllegalArgumentException("User already exists");
        }

        // Create and save the user
        User user = new User(username, email);
        userRepository.save(user);
        return user;
    }
}
