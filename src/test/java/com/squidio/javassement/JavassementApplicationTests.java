package com.squidio.javassement;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.squidio.javassement.request.StatementRequest;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.condition.EnabledOnJre;
import org.junit.jupiter.api.condition.JRE;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {JavassementApplication.class}
)
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("API ASSESEMENT TESTS")
class JavassementApplicationTests {

    @Autowired
     MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;


    @Test
    @DisplayName("Return All Users as Admin")
    @EnabledOnJre(value = JRE.JAVA_8,disabledReason = "Server Was Programmed to run on Java 8 Environment")
    @WithMockUser(roles = {"ADMIN"})
    void ReturnUsersAsAdmin() throws Exception {
        // PERFORM THE FOLLOWING CALL
        mockMvc.perform(
                MockMvcRequestBuilders.get("/api/v1/admin/assesement/users").contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        )
                .andExpect(MockMvcResultMatchers.status().isOk());


    }
    @Test
    @DisplayName("Return All Users as Normal User")
    @EnabledOnJre(value = JRE.JAVA_8,disabledReason = "Server Was Programmed to run on Java 8 Environment")
    @WithAnonymousUser
    void ReturnUsersAsNormalUser() throws Exception {
        // PERFORM THE FOLLOWING CALL
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/admin/assesement/users").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());


    }
    @Test
    @DisplayName("Return Specific User as Admin")
    @EnabledOnJre(value = JRE.JAVA_8,disabledReason = "Server Was Programmed to run on Java 8 Environment")
    @WithMockUser(roles = {"ADMIN"})
    void ReturnSpecificUser() throws Exception {
        // PERFORM THE FOLLOWING CALL
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/admin/assesement/users/Mohamed").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists());

    }
    @Test
    @DisplayName("Return Specific User as Normal User")
    @EnabledOnJre(value = JRE.JAVA_8,disabledReason = "Server Was Programmed to run on Java 8 Environment")
    @WithAnonymousUser
    void ReturnSpecificUserAsNormalUser() throws Exception {
        // PERFORM THE FOLLOWING CALL
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/admin/assesement/users/Mohamed").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());


    }

    @Test
    @DisplayName("Return Accounts Owned by User as Admin")
    @EnabledOnJre(value = JRE.JAVA_8,disabledReason = "Server Was Programmed to run on Java 8 Environment")
    @WithMockUser(roles = {"ADMIN"},username = "Admin")
    void ReturnAccountsOwned() throws Exception {
        // PERFORM THE FOLLOWING CALL
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/accounts/assesement/users/qbnKddlq70").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk());



    }

    @Test
    @DisplayName("Return Accounts Owned by User as Owner")
    @EnabledOnJre(value = JRE.JAVA_8,disabledReason = "Server Was Programmed to run on Java 8 Environment")
    @WithMockUser(username = "Mohamed")
    void ReturnAccountsOwnedAsOwner() throws Exception {
        // PERFORM THE FOLLOWING CALL
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/accounts/assesement/users/qbnKddlq70").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].iban").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountType").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].accountNumber").exists());


    }

    @Test
    @DisplayName("Return Accounts Owned by User as a Non-Owner")
    @EnabledOnJre(value = JRE.JAVA_8,disabledReason = "Server Was Programmed to run on Java 8 Environment")
    @WithMockUser(username = "John")
    void ReturnAccountsOwnedAsNonOwner() throws Exception {
        // PERFORM THE FOLLOWING CALL
        mockMvc.perform(
                        MockMvcRequestBuilders.get("/api/v1/accounts/assesement/users/qbnKddlq70").contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());



    }


    @Test
    @DisplayName("Return Statements as Owner of Account CbIJb0i3vQ")
    @EnabledOnJre(value = JRE.JAVA_8,disabledReason = "Server Was Programmed to run on Java 8 Environment")
    @WithMockUser(username = "Mohamed")
    void ReturnStatementsAsOwner() throws Exception {
        StatementRequest request=new StatementRequest();
        request.setAccountId("CbIJb0i3vQ");
        request.setFromDate("2020-05-01");
        request.setToDate("2020-06-01");
        String sendOffData=objectMapper.writeValueAsString(request);
        // PERFORM THE FOLLOWING CALL
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/accounts/assesement/statements").content(sendOffData).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].date").exists());

    }
    @Test
    @DisplayName("Return Statements as Non-Owner of Account CbIJb0i3vQ")
    @EnabledOnJre(value = JRE.JAVA_8,disabledReason = "Server Was Programmed to run on Java 8 Environment")
    @WithAnonymousUser
    void ReturnStatementsAsNonOwner() throws Exception {
        StatementRequest request=new StatementRequest();
        request.setAccountId("CbIJb0i3vQ");
        String sendOffData=objectMapper.writeValueAsString(request);
        // PERFORM THE FOLLOWING CALL
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/accounts/assesement/statements").content(sendOffData).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isUnauthorized());



    }
    @Test
    @DisplayName("Return Statements as Owner of Account CbIJb0i3vQ from 2020-05-01 to 2020-06-01")
    @EnabledOnJre(value = JRE.JAVA_8,disabledReason = "Server Was Programmed to run on Java 8 Environment")
    @WithMockUser(username = "Mohamed")
    void ReturnStatementsAsOwnerFromDateRange() throws Exception {
        StatementRequest request=new StatementRequest();
        request.setAccountId("CbIJb0i3vQ");
        request.setFromDate("2020-05-01");
        request.setToDate("2020-06-01");
        String sendOffData=objectMapper.writeValueAsString(request);
        // PERFORM THE FOLLOWING CALL
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/accounts/assesement/statements").content(sendOffData).contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON)
                )
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].amount").exists())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].date").exists());


    }


}
