package com.example.service;

import com.example.domain.User;
import com.example.exception.UserNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Stream;

import static com.example.service.UserService.ALL_USERS_URL;
import static com.example.service.UserService.USERS_IN_LONDON_URL;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class UserServiceTest {
    private final RestTemplateBuilder mockRestTemplateBuilder = mock(RestTemplateBuilder.class);
    private final RestTemplate mockRestTemplate = mock(RestTemplate.class);
    private UserService underTest;

    public static final User USER_IN_LONDON = new User(
        1, "firstName", "lastName", "emailAddress", "ip_address", 51.509, -0.118);
    public static final User USER_WITHIN_FIFTY_MILES_LONDON = new User(
        1, "firstName", "lastName", "emailAddress", "ip_address", 51.6710832, 0.8078532);
    public static final User USER_OUTSIDE_FIFTY_MILES_LONDON = new User(
        1, "firstName", "lastName", "emailAddress", "ip_address", 41.6710832, 0.8078532);

    @BeforeEach
    void setUp() {
        when(mockRestTemplateBuilder.build()).thenReturn(mockRestTemplate);
        underTest = new UserService(mockRestTemplateBuilder);
    }

    @Test
    void returnsUsersInAndAroundFiftyMilesOfLondon() {
        when(mockRestTemplate.getForEntity(USERS_IN_LONDON_URL, User[].class))
            .thenReturn(new ResponseEntity<>(Stream.of(USER_IN_LONDON).toArray(User[]::new), HttpStatus.OK));
        when(mockRestTemplate.getForEntity(ALL_USERS_URL, User[].class))
            .thenReturn(new ResponseEntity<>(
                Stream.of(USER_WITHIN_FIFTY_MILES_LONDON).toArray(User[]::new),
                HttpStatus.OK));
        User[] result = underTest.getUsersInOrAroundLondon();
        assertThat(result).containsExactly(USER_IN_LONDON, USER_WITHIN_FIFTY_MILES_LONDON);
    }

    @Test
    void filtersUsersInAndAroundFiftyMilesOfLondon() {
        when(mockRestTemplate.getForEntity(USERS_IN_LONDON_URL, User[].class))
            .thenReturn(new ResponseEntity<>(Stream.of(USER_IN_LONDON).toArray(User[]::new), HttpStatus.OK));
        when(mockRestTemplate.getForEntity(ALL_USERS_URL, User[].class))
            .thenReturn(new ResponseEntity<>(
                Stream.of(USER_WITHIN_FIFTY_MILES_LONDON, USER_OUTSIDE_FIFTY_MILES_LONDON).toArray(User[]::new),
                HttpStatus.OK));
        User[] result = underTest.getUsersInOrAroundLondon();
        assertThat(result).containsExactly(USER_IN_LONDON, USER_WITHIN_FIFTY_MILES_LONDON);
    }

    @Test
    void returnsUsersInLondonOnly() {
        when(mockRestTemplate.getForEntity(USERS_IN_LONDON_URL, User[].class))
            .thenReturn(new ResponseEntity<>(Stream.of(USER_IN_LONDON).toArray(User[]::new), HttpStatus.OK));
        when(mockRestTemplate.getForEntity(ALL_USERS_URL, User[].class))
            .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        User[] result = underTest.getUsersInOrAroundLondon();
        assertThat(result).containsExactly(USER_IN_LONDON);
    }

    @Test
    void returnsUsersWithinFiftyMilesOfLondonOnly() {
        when(mockRestTemplate.getForEntity(USERS_IN_LONDON_URL, User[].class))
            .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(mockRestTemplate.getForEntity(ALL_USERS_URL, User[].class))
            .thenReturn(new ResponseEntity<>(
                Stream.of(USER_WITHIN_FIFTY_MILES_LONDON).toArray(User[]::new),
                HttpStatus.OK));
        User[] result = underTest.getUsersInOrAroundLondon();
        assertThat(result).containsExactly(USER_WITHIN_FIFTY_MILES_LONDON);
    }

    @Test
    void throwsUserNotFoundExceptionWhenCallToUsersInLondonEndpointFails() {
        RestClientException restClientException = new RestClientException("message");
        when(mockRestTemplate.getForEntity(USERS_IN_LONDON_URL, User[].class))
            .thenThrow(restClientException);
        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(underTest::getUsersInOrAroundLondon)
            .withMessage("Could not find any user who lives in or within 50 miles od london because of : message");
    }

    @Test
    void throwsUserNotFoundExceptionWhenCallToAllUsersEndpointFails() {
        when(mockRestTemplate.getForEntity(USERS_IN_LONDON_URL, User[].class))
            .thenReturn(new ResponseEntity<>(Stream.of(USER_IN_LONDON).toArray(User[]::new), HttpStatus.OK));
        when(mockRestTemplate.getForEntity(ALL_USERS_URL, User[].class))
            .thenThrow(RestClientException.class);
        assertThatExceptionOfType(UserNotFoundException.class).isThrownBy(underTest::getUsersInOrAroundLondon);
    }

    @Test
    void returnsAnEmptyArray() {
        User[] emptyUserArray = new User[0];
        when(mockRestTemplate.getForEntity(USERS_IN_LONDON_URL, User[].class))
            .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(mockRestTemplate.getForEntity(ALL_USERS_URL, User[].class))
            .thenReturn(new ResponseEntity<>(emptyUserArray, HttpStatus.OK));
        User[] result = underTest.getUsersInOrAroundLondon();
        assertThat(result).isEmpty();
    }

    @Test
    void returnsNull() {
        when(mockRestTemplate.getForEntity(USERS_IN_LONDON_URL, User[].class))
            .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        when(mockRestTemplate.getForEntity(ALL_USERS_URL, User[].class))
            .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));
        User[] result = underTest.getUsersInOrAroundLondon();
        assertThat(result).isNull();
    }
}