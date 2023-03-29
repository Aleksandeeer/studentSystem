package com.example.studentSystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Student {
    private int id;
    private String name;
    private String surname;
    private int age;
    private String city;
    private String educationalDirection;
    List<String> dateList;
    List<String> subjectList;
    List<Integer> markList;
}
