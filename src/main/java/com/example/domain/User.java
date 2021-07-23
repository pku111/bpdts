package com.example.domain;

import java.io.Serializable;

public class User implements Serializable {
    private final int id;
    private final String first_name;
    private final String last_name;
    private final String email;
    private final String ip_address;
    private final double latitude;
    private final double longitude;

    public User(
        int id,
        String first_name,
        String last_name,
        String email,
        String ip_address,
        double latitude,
        double longitude
    ) {
        this.id = id;
        this.first_name = first_name;
        this.last_name = last_name;
        this.email = email;
        this.ip_address = ip_address;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getIp_address() {
        return ip_address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

//    @Override
//    public String toString() {
//        return "User{" +
//            "id=" + id +
//            ", first_name='" + first_name + '\'' +
//            ", last_name='" + last_name + '\'' +
//            ", email='" + email + '\'' +
//            ", ip_address='" + ip_address + '\'' +
//            ", latitude=" + latitude +
//            ", longitude=" + longitude +
//            '}';
//    }
}
