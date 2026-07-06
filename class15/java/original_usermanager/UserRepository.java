package com.tdd.tddmock.usermanager;

public interface UserRepository {
    boolean exists(String username);
    void save(User user);
}
