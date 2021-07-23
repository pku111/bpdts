package com.example.controller;

import com.example.domain.User;
import com.example.exception.UserNotFoundException;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;

    public static final User USER_IN_LONDON = new User(
        1, "firstName", "lastName", "emailAddress", "ip_address", 51.509, -0.118);
    public static final String USER_IN_LONDON_JSON = "[{" +
        "\"id\":1" +
        ",\"first_name\":\"firstName\"" +
        ",\"last_name\":\"lastName\"" +
        ",\"email\":\"emailAddress\"" +
        ",\"ip_address\":\"ip_address\"" +
        ",\"latitude\":51.509" +
        ",\"longitude\":-0.118}]";


    @Test
    void getUsersInOrAroundLondonFromService() throws Exception {
        when(userService.getUsersInOrAroundLondon()).thenReturn(new User[]{USER_IN_LONDON});
        this.mockMvc.perform(get("/users/london")).andExpect(status().isOk())
            .andExpect(content().string(containsString(USER_IN_LONDON_JSON)));
    }

    @Test
    void getEmptyArrayOfUsersFromService() throws Exception {
        when(userService.getUsersInOrAroundLondon()).thenReturn(new User[0]);
        this.mockMvc.perform(get("/users/london"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Could not find any user who lives in or within 50 miles of london")));
    }

    @Test
    void getNullFromService() throws Exception {
        when(userService.getUsersInOrAroundLondon()).thenReturn(null);
        this.mockMvc.perform(get("/users/london"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Could not find any user who lives in or within 50 miles of london")));
    }

    @Test
    void throwsExceptionFromService() throws Exception {
        UserNotFoundException userNotFoundException = new UserNotFoundException();
        when(userService.getUsersInOrAroundLondon()).thenThrow(userNotFoundException);
        this.mockMvc.perform(get("/users/london"))
            .andExpect(status().isNotFound())
            .andExpect(content().string(containsString("Could not find any user who lives in or within 50 miles of london")));
    }
}