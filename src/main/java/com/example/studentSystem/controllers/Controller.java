package com.example.studentSystem.controllers;

import com.example.studentSystem.models.Student;
import com.example.studentSystem.models.User;
import com.example.studentSystem.services.Service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class Controller {
    private final Service service;
    private int codeRole = 0;
    // 2 - ADMIN - полный доступ
    // 1 - TEACHER - только добавление оценок
    // 0 - USER (PARENT AND CHILD) - только просмотр оценок

    private boolean status = false;
    public int codeRole() {
        return codeRole;
    }

    @PostMapping("/login")
    public String login(Model model, User user) {
        status = service.isUserValid(user);
        if (status){
            // 2 - ADMIN - полный доступ
            // 1 - TEACHER - только добавление оценок
            // 0 - USER (PARENT AND STUDENT) - только просмотр оценок
            switch (user.getUserRole()) {
                case "admin" -> codeRole = 2;
                case "teacher" -> codeRole = 1;
                case "student", "parent" -> codeRole = 0;
            }

            model.addAttribute("codeRole", codeRole());
            model.addAttribute("user", user);
            model.addAttribute("students", service.listStudents());
            model.addAttribute("directions", service.listDirections());
            return students(model);
        } else {
            return "login";
        }
    }
    @GetMapping("/")
    public String students(Model model) {
        // Проверка на обход странички логина
        if (status) {
            model.addAttribute("codeRole", codeRole());
            model.addAttribute("students", service.listStudents());
            model.addAttribute("directions", service.listDirections());
            return "students";
        } else {
            return "login";
        }
    }

    @GetMapping("/student/{id}")
    public String studentInfo(@PathVariable int id, Model model) {
        model.addAttribute("codeRole", codeRole());
        model.addAttribute("student", service.getStudentById(id));
        return "student-info";
    }

    @PostMapping("/student/create")
    public String createStudent(Student student, Model model) {
        model.addAttribute("codeRole", codeRole());
        service.saveStudent(student);
        return "redirect:/";
    }

    @PostMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable int id, Model model) {
        model.addAttribute("codeRole", codeRole());
        service.deleteStudent(id);
        return "redirect:/";
    }

    @PostMapping("/student/insert/{id}")
    public String insertStudent(HttpServletRequest request, @PathVariable int id, Model model) {
        model.addAttribute("codeRole", codeRole());
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
