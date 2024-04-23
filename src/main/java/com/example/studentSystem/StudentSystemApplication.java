package com.example.studentSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class StudentSystemApplication {

    public static void main(String[] args) {
//		System.out.println(Hashing.sha256()
//				.hashString("temp2", StandardCharsets.UTF_8)
//				.toString());
        SpringApplication.run(StudentSystemApplication.class, args);
    }

}
