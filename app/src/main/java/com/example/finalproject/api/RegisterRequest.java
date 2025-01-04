package com.example.finalproject.api;

public class RegisterRequest {
    private String email;
    private String password;
    private String username;
    private String phone;
    private Contact contact;
    private String role;

    public RegisterRequest(String email, String password, String username, String phone, String address, String role) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.phone = phone;
        this.contact = new Contact(address);
        this.role = role;
    }

    public static class Contact {
        private String address;

        public Contact(String address) {
            this.address = address;
        }
    }
}
