package com.example.demo;

import com.example.demo.config.UserConfig;
import com.example.demo.controller.LoginController;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc
@Import({
		UserConfig.class,
		DemoApplicationTests.TestSecurityConfig.class
})

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
			.andExpect(redirectedUrl("/auth/login"));
	}

	@Test
	void testLoginRedirectToDefaultPage() throws Exception {

		MvcResult initialResult = mvc.perform(get("/home"))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/auth/login"))
				.andReturn();

		HttpSession httpSession = initialResult.getRequest().getSession();
		MockHttpSession session = (MockHttpSession) httpSession;
		assertThat(session).isNotNull();

		mvc.perform(MockMvcRequestBuilders.post("/auth/login_processing")
						.param("username", "admin")
						.param("password", "password")
						.with(SecurityMockMvcRequestPostProcessors.csrf())
						.session(session))
				.andExpect(status().is3xxRedirection())
				.andExpect(redirectedUrl("/home"));
	}

	@TestConfiguration
	@EnableWebSecurity
	static class TestSecurityConfig {

		@Bean
		SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
			http
				.csrf(Customizer.withDefaults())
				.authorizeHttpRequests(auth -> auth
					.requestMatchers(
							PathRequest.toStaticResources().atCommonLocations(),
							PathPatternRequestMatcher.withDefaults().matcher("/auth/**")
					).permitAll()
					.anyRequest().authenticated()
				)
				.formLogin(form -> form
					.loginPage("/auth/login")
					.loginProcessingUrl("/auth/login_processing")
					.defaultSuccessUrl("/home", true)
					.permitAll()
				)
				.logout(LogoutConfigurer::permitAll);

			return http.build();
		}

	}
}
