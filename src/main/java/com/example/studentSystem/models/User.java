package com.example.studentSystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    String UserLogin;
    String UserPasswordSHA256;
}
