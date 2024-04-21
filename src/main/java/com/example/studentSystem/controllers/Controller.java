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
    private int isAdmin = 0;
    private boolean status = false;

    @ModelAttribute("isAdmin")
    public int isAdmin() {
        return isAdmin;
    }

    @PostMapping("/login")
    public String login(Model model, User user) {
        status = service.isUserValid(user);
        if (status){
            if (user.getUserRole().equals("admin"))
                isAdmin = 1;
            else
                isAdmin = 0;

            model.addAttribute("isAdmin", isAdmin());
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
            model.addAttribute("students", service.listStudents());
            model.addAttribute("directions", service.listDirections());
            return "students";
        } else {
            return "login";
        }
    }

    @GetMapping("/student/{id}")
    public String studentInfo(@PathVariable int id, Model model) {
        model.addAttribute("isAdmin", isAdmin());
        model.addAttribute("student", service.getStudentById(id));
        return "student-info";
    }

    @PostMapping("/student/create")
    public String createStudent(Student student, Model model) {
        model.addAttribute("isAdmin", isAdmin());
        service.saveStudent(student);
        return "redirect:/";
    }

    @PostMapping("/student/delete/{id}")
    public String deleteStudent(@PathVariable int id, Model model) {
        model.addAttribute("isAdmin", isAdmin());
        service.deleteStudent(id);
        return "redirect:/";
    }

    @PostMapping("/student/insert/{id}")
    public String insertStudent(HttpServletRequest request, @PathVariable int id, Model model) {
        model.addAttribute("isAdmin", isAdmin());
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
