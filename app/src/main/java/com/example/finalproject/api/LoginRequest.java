package com.example.finalproject.api;

// Lớp chứa thông tin đăng nhập
public class LoginRequest {
    String email;
    String password;

    // Constructor để khởi tạo email và password
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}

