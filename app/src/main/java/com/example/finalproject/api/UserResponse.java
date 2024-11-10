package com.example.finalproject.api;

public class UserResponse {
    private String _id;
    private String username;
    private String email;
    private String phone;
    private String role;
    private Contact contact;

    public static class Contact {
        private Coordinates coordinates;
        private String email;
        private String address;
        private String building;
        private String emergencyContact;

        public static class Coordinates {
            private double lat;
            private double lng;
        }
    }

    // Getter and setter methods
    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }
}
