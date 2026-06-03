package com.racingquiz.backend.security;

import com.racingquiz.backend.config.SecurityConfig;
import com.racingquiz.backend.controller.AdminController;
import com.racingquiz.backend.dto.AdminOverviewDto;
import com.racingquiz.backend.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
@Import({SecurityConfig.class, JwtAuthenticationFilter.class, JwtTokenService.class, RestAuthenticationEntryPoint.class})
@TestPropertySource(properties = {
        "admin.username=admin",
        "admin.password=666666",
        "jwt.secret=0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef",
        "jwt.expiration-minutes=60"
})
public class AdminSecurityTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtTokenService jwtTokenService;

    @MockBean
    private AdminService adminService;

    @Test
    void adminEndpointsRequireToken() throws Exception {
        mockMvc.perform(get("/api/admin/overview"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void adminEndpointsAcceptAdminToken() throws Exception {
        when(adminService.getOverview()).thenReturn(new AdminOverviewDto(1L, 2L, 3L));
        String token = jwtTokenService.createToken("admin", "ADMIN");

        mockMvc.perform(get("/api/admin/overview").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userCount").value(2));
    }

    @Test
    void adminLoginReturnsToken() throws Exception {
        when(adminService.login("admin", "666666")).thenReturn(true);

        mockMvc.perform(post("/api/admin/login")
                        .contentType("application/json")
                        .content("{\"username\":\"admin\",\"password\":\"666666\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }
}

