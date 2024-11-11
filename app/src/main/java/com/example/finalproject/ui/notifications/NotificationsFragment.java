package com.example.finalproject.ui.notifications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.LoginActivity;
import com.example.finalproject.api.ApiService;
import com.example.finalproject.api.UserResponse;
import com.example.finalproject.databinding.FragmentNotificationsBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textUsername = binding.textUsername;
        final TextView textEmail = binding.textEmail;
        final TextView textContactInfo = binding.textContactInfo;
        final TextView textDevices = binding.textDevices;
        Button buttonLogout = binding.buttonLogout;

        String token = getTokenFromSharedPreferences();

        if (token != null) {
            ApiService.apiService.getUserProfile("Bearer " + token).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse user = response.body();

                        textUsername.setText("Username: " + user.getUsername());
                        textEmail.setText("Email: " + user.getEmail());

                        String contactInfo = "Contact Info:\n" +
                                "Email: " + user.getContact().getEmail() + "\n" +
                                "Address: " + user.getContact().getAddress() + "\n" +
                                "Building: " + user.getContact().getBuilding() + "\n" +
                                "Emergency Contact: " + user.getContact().getEmergencyContact() + "\n" +
                                "Coordinates: " + user.getContact().getCoordinates().getLat() +
                                ", " + user.getContact().getCoordinates().getLng();
                        textContactInfo.setText(contactInfo);

                        StringBuilder devicesInfo = new StringBuilder("Devices:\n");
                        for (UserResponse.Device device : user.getDevices()) {
                            devicesInfo.append("Device ID: ").append(device.getDeviceId()).append("\n");
                        }
                        textDevices.setText(devicesInfo.toString());

                    } else {
                        textContactInfo.setText("Failed to fetch user data.");
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    textContactInfo.setText("Error: " + t.getMessage());
                }
            });
        } else {
            Toast.makeText(getContext(), "No token found.", Toast.LENGTH_SHORT).show();
        }

        buttonLogout.setOnClickListener(v -> {
            // Xóa token khỏi SharedPreferences
            SharedPreferences preferences = requireActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("token");
            editor.apply();

            Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

            // Chuyển đến LoginActivity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return root;
    }

    private String getTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
