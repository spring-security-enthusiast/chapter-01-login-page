package com.example.demo;

import com.example.demo.controller.LoginController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc(addFilters = false) // flip to true when you want to test security/CSRF
class DemoApplicationTests {

	@Autowired
	MockMvc mvc;

	@Test
	void testCustomLoginPageExistence() throws Exception {
		mvc
			.perform(MockMvcRequestBuilders.get("/auth/login"))
			.andExpect(status().isOk());
	}

	@Test
	void testHomePage302RedirectToLoginPage() throws Exception {
		mvc
			.perform(MockMvcRequestBuilders.get("/index"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("**/auth/login"));
	}

	@Test
	void testLoginRedirectToDefaultPage() throws Exception {
		mvc.perform(formLogin("/auth/login")
			.user("admin")
			.password("password"))
		.andExpect(status().is3xxRedirection())
		.andExpect(MockMvcResultMatchers.redirectedUrl("/"));
	}

}
