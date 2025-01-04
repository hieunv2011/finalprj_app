package com.example.finalproject.api;

public class VerifyOtpRequest {
    private String email;
    private String password;
    private String username;
    private String phone;
    private Contact contact;
    private String role;
    private String otp;

    public VerifyOtpRequest(String email, String password, String username, String phone, String address, String role, String otp) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.phone = phone;
        this.contact = new Contact(address);
        this.role = role;
        this.otp = otp;
    }

    public static class Contact {
        private String address;

        public Contact(String address) {
            this.address = address;
        }
    }
}