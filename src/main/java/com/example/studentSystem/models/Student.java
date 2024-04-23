package com.example.studentSystem.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Student {
    public List<String> dateList;
    public List<String> subjectList;
    public List<Integer> markList;
    private int id;
    private String name;
    private String surname;
    private int age;
    private String city;
    private String educationalDirection;
}
