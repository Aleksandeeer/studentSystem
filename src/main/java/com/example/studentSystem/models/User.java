package com.example.studentSystem.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@NoArgsConstructor
public class User {
    String UserLogin;
    String UserPasswordSHA256;
    String UserRole;

    @Setter
    int StudentID;

    public User(String UserLogin, String UserPasswordSHA256, String UserRole) {
        this.UserLogin = UserLogin;
        this.UserPasswordSHA256 = UserPasswordSHA256;
        this.UserRole = UserRole;
    }

}
