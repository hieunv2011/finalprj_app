package com.example.finalproject.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.api.ApiService;
import com.example.finalproject.api.UserResponse;
import com.example.finalproject.databinding.FragmentHomeBinding;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Khởi tạo ViewModel
        HomeViewModel HomeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        // Inflate layout
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // TextView để hiển thị thông tin
        final TextView textView = binding.textHome;
        final TextView textUserInfo = binding.textUserInfo;

        // Lấy token từ SharedPreferences
        String token = getTokenFromSharedPreferences();

        // Kiểm tra và hiển thị token
        if (token != null) {
            textView.setText("Token: " + token);  // Hiển thị token

            // Gọi API để lấy thông tin người dùng
            ApiService.apiService.getUserProfile("Bearer " + token).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse user = response.body();
                        textUserInfo.setText("User Info:\n" + "Username: " + user.getUsername() + "\nEmail: " + user.getEmail());
                    } else {
                        textUserInfo.setText("Failed to fetch user data.");
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    textUserInfo.setText("Error: " + t.getMessage());
                }
            });
        } else {
            textView.setText("No token found.");  // Nếu không có token, hiển thị thông báo
        }

        return root;
    }

    private String getTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        // Lấy giá trị của token
        return sharedPreferences.getString("token", null);  // Nếu không có token, trả về null
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
