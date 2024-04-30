package com.example.studentSystem.controllers;

import com.example.studentSystem.models.Student;
import com.example.studentSystem.models.User;
import com.example.studentSystem.services.Service;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

@org.springframework.stereotype.Controller
@RequiredArgsConstructor
public class Controller {
    private final Service service;

    // ? Использование CACHE
    private static Cache<String, String> cache;
    static {
        cache = CacheBuilder.newBuilder()
                .maximumSize(100) // максимальное количество элементов в кэше
                .expireAfterWrite(10, TimeUnit.MINUTES) // время жизни элементов кэша
                .build();
    }
    private int codeRole = 0;
    // 2 - ADMIN - полный доступ
    // 1 - TEACHER - только добавление оценок
    // 0 - USER (PARENT AND CHILD) - только просмотр оценок

    private boolean status = false;

    public int codeRole() {
        return codeRole;
    }

    @PostMapping("/login")
    public String login(Model model, User user, HttpServletResponse response, HttpServletRequest request) {
        status = service.isUserValid(user);
        if (status) {
            // * 2 - ADMIN - полный доступ
            // * 1 - TEACHER - только добавление оценок
            // * 0 - USER (PARENT AND STUDENT) - только просмотр оценок
            switch (user.getUserRole()) {
                case "admin" -> codeRole = 2;
                case "teacher" -> codeRole = 1;
                case "student", "parent" -> codeRole = 0;
            }

            // ? Использование CACHE
            addToCache("codeRole", String.valueOf(codeRole()));

            // ? Использование COOKIE
            // создание COOKIE и установка его значения
            Cookie roleCookie = new Cookie("codeRole", String.valueOf(codeRole));
            response.addCookie(roleCookie);

            model.addAttribute("codeRole", codeRole());
            model.addAttribute("user", user);
            model.addAttribute("students", service.listStudents());
            model.addAttribute("directions", service.listDirections());
            return students(model, request);
        } else {
            return "login";
        }
    }

    @GetMapping("/")
    public String students(Model model, HttpServletRequest request) {
        // ? Использование COOKIE
        Cookie[] cookies = request.getCookies();
        int userRole = 0;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("codeRole")) {
                    userRole = Integer.parseInt(cookie.getValue());
                    break;
                }
            }
        }

        // Проверка на обход странички логина
        if (status) {
            model.addAttribute("codeRole", userRole);
            model.addAttribute("students", service.listStudents());
            model.addAttribute("directions", service.listDirections());
            return "students";
        } else {
            return "login";
        }
    }

    @GetMapping("/teacher")
    public String add_teacher(Model model, HttpServletRequest request) {
        List<User> listTeacher = service.listTeacher();

        // ? Использование CACHE
        int Role = Integer.parseInt(getFromCache("codeRole"));

        if (Role == 2 && status) {
            model.addAttribute("teachers", service.listTeacher());
            model.addAttribute("codeRole", codeRole());
            // ? Использование SESSION
            HttpSession session = request.getSession();
            session.setAttribute("codeRole", codeRole());
            return "teacher";
        } else {
            status = false;
            codeRole = 0;
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

    @PostMapping("/teacher/create")
    public String createTeacher(User user, Model model, HttpServletRequest request) {
        // Установка оставшихся параметров (по факту установка по умолчанию для данного кейса)
        // ? Использование SESSION
        HttpSession session = request.getSession();
        int userRole = (int) session.getAttribute("codeRole");
        if (userRole == 2) {
            user.setStudentID(0);
            user.setUserRole("teacher");

            model.addAttribute("codeRole", codeRole());
            service.saveTeacher(user);
        }
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
    public String[] getSubjects(@RequestParam("direction") String educationDirection, HttpServletRequest request) {
        // ? Использование COOKIE
        Cookie[] cookies = request.getCookies();
        int userRole = 0;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("userRole")) {
                    userRole = Integer.parseInt(cookie.getValue());
                    break;
                }
            }
        }

        if (userRole == 0 || userRole == 1 || userRole == 2)
            return service.getSubjectsByDirection(educationDirection);
        else
            return null;
    }

    // ? Использование CACHE
    public static void addToCache(String key, String value) {
        cache.put(key, value);
    }

    // ? Использование CACHE
    public static String getFromCache(String key) {
        return cache.getIfPresent(key);
    }
}
