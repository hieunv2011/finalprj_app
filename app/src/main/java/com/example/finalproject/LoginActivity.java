package com.example.finalproject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.api.ApiService;
import com.example.finalproject.api.LoginRequest;
import com.example.finalproject.api.LoginResponse;
import com.example.finalproject.api.RegisterRequest;
import com.example.finalproject.api.UserDevicesResponse;
import com.example.finalproject.api.UserProfileRequest;
import com.example.finalproject.api.UserResponse;
import com.example.finalproject.api.VerifyOtpRequest;
import com.example.finalproject.api.VerifyOtpResponse;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {

    private EditText usernameEditText;
    private EditText passwordEditText;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        TextView registerTextView = findViewById(R.id.register);
        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showRegisterDialog();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginUser(email, password);
            }
        });
    }

    private void showRegisterDialog() {
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_register, null);

        EditText emailEditText = dialogView.findViewById(R.id.emailEditText);
        EditText usernameEditText = dialogView.findViewById(R.id.usernameEditText);
        EditText passwordEditText = dialogView.findViewById(R.id.passwordEditText);
        EditText phoneEditText = dialogView.findViewById(R.id.phoneEditText);
        EditText addressEditText = dialogView.findViewById(R.id.addressEditText);
        EditText otpEditText = new EditText(this); // Thêm EditText mới cho OTP
        otpEditText.setHint("Nhập mã OTP");

        Button verifyOtpButton = new Button(this); // Nút xác nhận OTP
        verifyOtpButton.setText("Xác nhận OTP");

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dialogView);
        builder.setTitle("Đăng ký tài khoản");
        builder.setPositiveButton("Xác nhận", null);
        builder.setNegativeButton("Hủy", (dialogInterface, i) -> dialogInterface.dismiss());

        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_bg);
        dialog.setOnShowListener(dialogInterface -> {
            Button saveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            saveButton.setOnClickListener(view -> {
                String email = emailEditText.getText().toString();
                String username = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                String phone = phoneEditText.getText().toString();
                String address = addressEditText.getText().toString();

                if (email.isEmpty() || username.isEmpty() || password.isEmpty() || phone.isEmpty() || address.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                } else {
                    // Tạo đối tượng RegisterRequest
                    RegisterRequest user = new RegisterRequest(email, password, username, phone, address, "user");

                    // Gọi API đăng ký người dùng
                    ApiService.apiService.register(user).enqueue(new Callback<UserResponse>() {
                        @Override
                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                            if (response.isSuccessful()) {
                                // Hiển thị thông báo OTP đã gửi
                                Toast.makeText(LoginActivity.this, "Đã gửi OTP về email: " + email, Toast.LENGTH_SHORT).show();

                                // Thêm trường nhập OTP và nút xác nhận OTP vào giao diện
                                if (otpEditText.getParent() == null) {
                                    ((ViewGroup) dialogView).addView(otpEditText);
                                }
                                if (verifyOtpButton.getParent() == null) {
                                    ((ViewGroup) dialogView).addView(verifyOtpButton);
                                }
                                otpEditText.setVisibility(View.VISIBLE);
                                verifyOtpButton.setVisibility(View.VISIBLE);

                                // Xử lý khi người dùng ấn nút xác nhận OTP
                                verifyOtpButton.setOnClickListener(v -> {
                                    String otp = otpEditText.getText().toString();
                                    if (otp.isEmpty()) {
                                        Toast.makeText(LoginActivity.this, "Vui lòng nhập mã OTP!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Tạo đối tượng VerifyOtpRequest với OTP
                                        VerifyOtpRequest otpRequest = new VerifyOtpRequest(email, password, username, phone, address, "user", otp);
                                        String otpRequestJson = new Gson().toJson(otpRequest);  // Chuyển đối tượng thành JSON
                                        Log.d("VerifyOtpRequest", otpRequestJson);
                                        // Gọi API verify OTP
                                        ApiService.apiService.verifyOtp(otpRequest).enqueue(new Callback<VerifyOtpResponse>() {
                                            @Override
                                            public void onResponse(Call<VerifyOtpResponse> call, Response<VerifyOtpResponse> response) {
                                                if (response.isSuccessful()) {
                                                    // Xử lý khi xác nhận OTP thành công
                                                    Toast.makeText(LoginActivity.this, "Xác nhận OTP thành công!", Toast.LENGTH_SHORT).show();
                                                    dialog.dismiss();
                                                } else {
                                                    // Xử lý khi xác nhận OTP thất bại
                                                    Toast.makeText(LoginActivity.this, "Xác nhận OTP thất bại!", Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<VerifyOtpResponse> call, Throwable t) {
                                                // Xử lý lỗi kết nối
                                                Toast.makeText(LoginActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                                            }
                                        });

                                    }
                                });
                            } else {
                                // Xử lý khi đăng ký thất bại
                                Toast.makeText(LoginActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<UserResponse> call, Throwable t) {
                            // Xử lý lỗi kết nối
                            Toast.makeText(LoginActivity.this, "Lỗi kết nối!", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        });
        dialog.show();
    }

    private void loginUser(String email, String password) {
        ApiService apiService = ApiService.apiService;
        LoginRequest loginRequest = new LoginRequest(email, password);

        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getToken();  // Lấy token từ phản hồi

                    // Lưu token vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", token);  // Lưu token
                    editor.apply();

                    // Lấy fcmtoken
                    FirebaseMessaging.getInstance().getToken()
                            .addOnSuccessListener(new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(String fcmToken) {
                                    // Cập nhật fcmtoken vào profile người dùng
                                    updateUserProfileWithFCM(token, fcmToken);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(LoginActivity.this, "Lỗi lấy FCM Token", Toast.LENGTH_SHORT).show();
                            });

                    // Gọi API lấy thông tin người dùng
                    fetchUserProfile(token);

                    // Chuyển hướng sang MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUserProfileWithFCM(String token, String fcmToken) {
        ApiService apiService = ApiService.apiService;

        // Lấy thông tin user từ SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", null);  // Lấy userId từ SharedPreferences

        if (userId != null) {
            // Tạo UserProfileRequest với fcmToken
            UserProfileRequest userProfileRequest = new UserProfileRequest();
            userProfileRequest.setFcmtoken(fcmToken);
            // Gửi yêu cầu cập nhật thông tin người dùng
            apiService.updateUserProfile("Bearer "+token, userId, userProfileRequest).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful()) {
                        // Thông báo cập nhật thành công
//                        Toast.makeText(LoginActivity.this, "Cập nhật thông tin thành công", Toast.LENGTH_SHORT).show();
                    } else {
//                        Toast.makeText(LoginActivity.this, "Cập nhật thông tin thất bại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "Lỗi cập nhật thông tin: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    private void fetchUserProfile(String token) {
        ApiService.apiService.getUserProfile("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    String userId = userResponse.getId();
                    // Lưu tất cả thông tin từ userDevicesResponse vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("userId", userId);  // Lưu vào SharedPreferences
                    editor.apply();
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchUserDevices(String token, String userId) {
        ApiService.apiService.getUserDevices("Bearer " + token, userId).enqueue(new Callback<UserDevicesResponse>() {
            @Override
            public void onResponse(Call<UserDevicesResponse> call, Response<UserDevicesResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserDevicesResponse userDevicesResponse = response.body();

                    // Lưu tất cả thông tin từ userDevicesResponse vào SharedPreferences
                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    // Chuyển đổi danh sách thiết bị thành chuỗi JSON
                    Gson gson = new Gson();
                    String devicesJson = gson.toJson(userDevicesResponse.getDevices());
                    editor.putString("user_devices", devicesJson);  // Lưu vào SharedPreferences
                    editor.apply();

                    // Log thông tin đã lưu trong SharedPreferences
                    Log.d("SharedPreferences", "Token: " + sharedPreferences.getString("token", "No token"));
                    Log.d("SharedPreferences", "User Devices: " + sharedPreferences.getString("user_devices", "No devices"));
                }
            }

            @Override
            public void onFailure(Call<UserDevicesResponse> call, Throwable t) {
                // Handle failure
            }
        });
    }

    private static class UserRegistration {
        private String email;
        private String username;
        private String password;
        private String phone;
        private String address;

        public UserRegistration(String email, String username, String password, String phone, String address) {
            this.email = email;
            this.username = username;
            this.password = password;
            this.phone = phone;
            this.address = address;
        }
    }
}
