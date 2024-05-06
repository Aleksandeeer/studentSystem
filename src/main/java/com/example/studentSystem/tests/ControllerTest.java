package com.example.studentSystem.tests;

import com.example.studentSystem.controllers.Controller;
import com.example.studentSystem.models.User;
import com.example.studentSystem.services.Service;
import com.google.common.hash.Hashing;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.Model;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class ControllerTest {

    @Mock
    private Service service;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @InjectMocks
    private Controller controller;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testLoginValidUser() {
        User user = new User();
        user.setUserLogin("temp4");
        user.setUserPasswordSHA256(Hashing.sha256()
				.hashString("temp4", StandardCharsets.UTF_8)
				.toString());

        when(service.isUserValid(any(User.class))).thenReturn(true);

        String result = controller.login(model, user, response, request);

        assertEquals("students", result);
        verify(model, times(1)).addAttribute(eq("codeRole"), anyInt());
        verify(service, times(1)).listStudents();
    }

    @Test
    public void testLoginInvalidUser() {
        User user = new User();
        user.setUserLogin("invalid_user");
        user.setUserPasswordSHA256(Hashing.sha256()
                .hashString("invalid_user", StandardCharsets.UTF_8)
                .toString());

        when(service.isUserValid(any(User.class))).thenReturn(false);

        String result = controller.login(model, user, response, request);

        assertEquals("login", result);
        verify(service, never()).listStudents(); // Проверяем, что метод listStudents() никогда не был вызван
    }


    // Тесты для других методов контроллера могут быть добавлены здесь
}
