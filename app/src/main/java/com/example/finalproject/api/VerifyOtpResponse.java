package com.example.finalproject.api;

public class VerifyOtpResponse {
    private String message;
    private boolean success;

    // Constructor
    public VerifyOtpResponse(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    // Getter và Setter
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
