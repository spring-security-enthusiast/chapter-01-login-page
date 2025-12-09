package com.example.demo;

import com.example.demo.config.UserConfig;
import com.example.demo.controller.LoginController;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.DisplayName;
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
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.servlet.util.matcher.PathPatternRequestMatcher;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(LoginController.class)
@AutoConfigureMockMvc
@Import({
        UserConfig.class,
        LoginFailureUrlTest.TestSecurityConfig.class
})
public class LoginFailureUrlTest {

    @Autowired
    MockMvc mvc;


    @Test
    @DisplayName("Should show error message Bad credentials")
    void testLoginFailureBadCredentialsTest() throws Exception {

        // Step 1: Access protected resource to get redirected to login
        MvcResult initialResult = mvc.perform(get("/home"))
                .andExpect(status().is3xxRedirection())
                // safer in slice tests – allows for possible context path
                .andExpect(redirectedUrl("/auth/login"))
                .andReturn();


        HttpSession httpSession = initialResult.getRequest().getSession();
        MockHttpSession session = (MockHttpSession) httpSession;
        assertThat(session).isNotNull();

        // Step 2: Perform failed login
        mvc.perform(MockMvcRequestBuilders.post("/auth/login_processing")
                        .param("username", "admin")
                        .param("password", "wrong_password")
                        .with(csrf())
                        .session(session))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth/login?error=true"));

        // Step 3: Request the login page with error parameter
        mvc.perform(get("/auth/login")
                        .param("error", "true")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Invalid username or password")))
                .andDo(print());
    }


    @TestConfiguration
    @EnableWebSecurity
    static class TestSecurityConfig {

        @Bean
        SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http
                    .csrf(Customizer.withDefaults())
                    .authorizeHttpRequests(auth -> auth
                            // allow login endpoints
                            .requestMatchers(
                                    PathRequest.toStaticResources().atCommonLocations(),
                                    PathPatternRequestMatcher.withDefaults().matcher("/auth/**"),
                                    PathPatternRequestMatcher.withDefaults().matcher("/error/**")
                            ).permitAll()
                            .anyRequest().authenticated()
                    )
                    .formLogin(form -> form
                            .loginPage("/auth/login")
                            .loginProcessingUrl("/auth/login_processing")
                            .defaultSuccessUrl("/home", true)
                            .failureUrl("/auth/login?error=true")
                            .permitAll()
                    )
                    .logout(LogoutConfigurer::permitAll);

            return http.build();
        }

    }
}
