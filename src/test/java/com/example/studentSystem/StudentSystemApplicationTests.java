package com.example.studentSystem;

import com.example.studentSystem.controllers.Controller;
import com.example.studentSystem.models.User;
import com.example.studentSystem.services.Service;
import com.google.common.hash.Hashing;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ui.Model;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest
class StudentSystemApplicationTests {

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

}
