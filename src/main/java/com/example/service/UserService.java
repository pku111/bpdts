package com.example.service;

import com.example.domain.User;
import com.example.exception.UserNotFoundException;
import net.sf.geographiclib.Geodesic;
import net.sf.geographiclib.GeodesicData;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

@Service
public class UserService {
    private final RestTemplate restTemplate;

    public static final String USERS_IN_LONDON_URL = "https://bpdts-test-app.herokuapp.com/city/london/users";
    public static final String ALL_USERS_URL = "https://bpdts-test-app.herokuapp.com/users";

    public UserService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    public User[] getUsersInOrAroundLondon() {
        Optional<User[]> userInLondon = getUsers(USERS_IN_LONDON_URL);
        Optional<User[]> allUsers = getUsers(ALL_USERS_URL);
        User[] usersWithinFiftyMilesOfLondon = allUsers.map(this::getUserWithinFiftyMilesOfLondon).orElse(null);
        if (userInLondon.isPresent() && usersWithinFiftyMilesOfLondon != null) {
            return Stream.of(userInLondon.get(), usersWithinFiftyMilesOfLondon)
                .flatMap(Stream::of).toArray(User[]::new);
        }
        return userInLondon.orElse(usersWithinFiftyMilesOfLondon);
    }

    private Optional<User[]> getUsers(String uri) {
        try {
            ResponseEntity<User[]> response = this.restTemplate.getForEntity(uri, User[].class);
            if (response.getStatusCode() == HttpStatus.OK) {
                return Optional.ofNullable(response.getBody());
            }
            return Optional.empty();
        } catch (RestClientException ex) {
            throw new UserNotFoundException(ex);
        }

    }

    private User[] getUserWithinFiftyMilesOfLondon(User[] allUsers) {
        return Arrays.stream(allUsers)
            .filter(user -> isWithinFiftyMiles(user.getLatitude(), user.getLongitude()))
            .toArray(User[]::new);
    }

    private boolean isWithinFiftyMiles(double userLat, double userLon) {
        // 51 deg 30 min 26 sec N
        double londonLat = 51 + (30 / 60.0) + (26 / 60.0 / 60.0);
        // 0 deg 7 min 39 sec W
        double londonLon = 0 - (7 / 60.0) - (39 / 60.0 / 60.0);

        GeodesicData result =
            Geodesic.WGS84.Inverse(londonLat, londonLon, userLat, userLon);
        double distanceInMeters = result.s12;
        double distanceInMiles = distanceInMeters / 1609.34;
        return  distanceInMiles <= 50;
    }
}
