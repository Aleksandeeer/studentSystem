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

    public Student(int id, String name, String surname, int age, String city, String educationalDirection, List<String> dateList,
                   List<String> subjectList, List<Integer> markList) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.age = age;
        this.city = city;
        this.educationalDirection = educationalDirection;
        this.dateList = dateList;
        this.subjectList = subjectList;
        this.markList = markList;
    }
}
