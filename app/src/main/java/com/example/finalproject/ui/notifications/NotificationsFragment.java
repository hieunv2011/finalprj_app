package com.example.finalproject.ui.notifications;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.finalproject.utils.Ultis;

import java.util.List;

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
        final TextView textId = binding.textId;
        Button buttonLogout = binding.buttonLogout;
        Button btnToken = binding.btnToken;

        String token = getTokenFromSharedPreferences();

        if (token != null) {
            ApiService.apiService.getUserProfile("Bearer " + token).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse user = response.body();

                        textUsername.setText(user.getUsername());
                        textEmail.setText("Email: " + user.getEmail());
                        textId.setText("id: " + user.getId());

                        String contactInfo = "Địa chỉ: " + user.getContact().getAddress();
                        textContactInfo.setText(contactInfo);

                        StringBuilder devicesInfo = new StringBuilder();
                        List<UserResponse.Device> devices = user.getDevices();
                        for (int i = 0; i < devices.size(); i++) {
                            // Lấy số ID từ chuỗi, giả sử "device003" cần chuyển thành "003"
                            String deviceId = devices.get(i).getDeviceId().replaceAll("[^0-9]", ""); // Loại bỏ tất cả ký tự không phải số
                            devicesInfo.append("device").append(String.format("%03d", Integer.parseInt(deviceId))); // Định dạng số với 3 chữ số

                            if (i < devices.size() - 1) {
                                devicesInfo.append(","); // Thêm dấu phẩy giữa các device trừ device cuối
                            }
                        }
                        devicesInfo.append("."); // Thêm dấu chấm ở cuối
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
        btnToken.setOnClickListener(v->{
            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String token) {
                            Log.d(Ultis.TAG,token);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(),"Fail",Toast.LENGTH_LONG).show();
                    });
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
