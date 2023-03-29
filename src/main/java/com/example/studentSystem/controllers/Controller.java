package com.example.studentSystem.controllers;

import com.example.studentSystem.models.Student;
import com.example.studentSystem.services.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class Controller {
    private final Service service;

    @GetMapping("/")
    public String students(Model model) {
        model.addAttribute("students", service.listStudents());
        model.addAttribute("directions", service.listDirections());
        return "students";
    }

    @GetMapping("/student/{id}")
    public String studentInfo(@PathVariable int id, Model model) {
        model.addAttribute("student", service.getStudentById(id));
        return "student-info";
    }

    @PostMapping("/student/create")
    public String createStudent(Student student) {
        service.saveStudent(student);
        return "redirect:/";
    }

    @PostMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable int id) {
        service.deleteStudent(id);
        return "redirect:/";
    }

    @PostMapping("/student/insert/{id}")
    public String insertStudent(HttpServletRequest request, @PathVariable int id) {
        String date = request.getParameter("Date");
        String subject = request.getParameter("Subject");
        int mark = Integer.parseInt(request.getParameter("Mark"));
        service.insertMark(date, subject, mark, id);
        return "redirect:/";
    }

    @GetMapping("/subjects")
    @ResponseBody
    public String[] getSubjects(@RequestParam("direction") String educationDirection) {
        return service.getSubjectsByDirection(educationDirection);
    }

}
