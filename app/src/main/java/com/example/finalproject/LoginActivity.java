//package com.example.finalproject;
//
//import android.content.Intent;
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import androidx.activity.EdgeToEdge;
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.example.finalproject.api.ApiService;
//import com.example.finalproject.api.LoginRequest;
//import com.example.finalproject.api.LoginResponse;
//
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//
//public class LoginActivity extends AppCompatActivity {
//
//    private EditText usernameEditText;
//    private EditText passwordEditText;
//    private Button loginButton;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        // Kiểm tra trạng thái đăng nhập
//        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
//        String token = sharedPreferences.getString("token", null);
//
//        if (token != null) {
//            // Token đã tồn tại, chuyển hướng trực tiếp sang MainActivity
//            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//            startActivity(intent);
//            finish();
//            return;
//        }
//
//        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_login);
//
//        usernameEditText = findViewById(R.id.usernameEditText);
//        passwordEditText = findViewById(R.id.passwordEditText);
//        loginButton = findViewById(R.id.loginButton);
//
//        loginButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String email = usernameEditText.getText().toString();
//                String password = passwordEditText.getText().toString();
//                loginUser(email, password);
//            }
//        });
//    }
//
//    private void loginUser(String email, String password) {
//        ApiService apiService = ApiService.apiService;
//        LoginRequest loginRequest = new LoginRequest(email, password);
//
//        apiService.login(loginRequest).enqueue(new Callback<LoginResponse>() {
//            @Override
//            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                if (response.isSuccessful() && response.body() != null) {
//                    String token = response.body().getToken();  // Lấy token từ phản hồi
//
//                    // Lưu token vào SharedPreferences
//                    SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
//                    SharedPreferences.Editor editor = sharedPreferences.edit();
//                    editor.putString("token", token);  // Lưu token vào file shared preferences
//                    editor.apply();  // Áp dụng thay đổi
//
//                    // Chuyển hướng sang MainActivity
//                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//                    startActivity(intent);
//                    finish();  // Kết thúc LoginActivity để không quay lại khi nhấn nút back
//                } else {
//                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<LoginResponse> call, Throwable t) {
//                Toast.makeText(LoginActivity.this, "Lỗi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//}
package com.example.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.finalproject.api.ApiService;
import com.example.finalproject.api.LoginRequest;
import com.example.finalproject.api.LoginResponse;
import com.example.finalproject.api.UserDevicesResponse;
import com.example.finalproject.api.UserResponse;
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

        // Kiểm tra trạng thái đăng nhập
        SharedPreferences sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE);
        String token = sharedPreferences.getString("token", null);

        if (token != null) {
            // Token đã tồn tại, chuyển hướng trực tiếp sang MainActivity
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = usernameEditText.getText().toString();
                String password = passwordEditText.getText().toString();
                loginUser(email, password);
            }
        });
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
                    editor.putString("token", token);  // Lưu token vào file shared preferences
                    editor.apply();  // Áp dụng thay đổi

                    // Fetch user profile and devices after login
                    fetchUserProfile(token);

                    // Chuyển hướng sang MainActivity
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();  // Kết thúc LoginActivity để không quay lại khi nhấn nút back
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

    private void fetchUserProfile(String token) {
        ApiService.apiService.getUserProfile("Bearer " + token).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    UserResponse userResponse = response.body();
                    String userId = userResponse.getId();
                    fetchUserDevices(token, userId);
                }
            }

            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
                // Handle failure
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
}
