package com.example.finalproject.ui.notifications;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Khởi tạo ViewModel
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        // Inflate layout
        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // TextView để hiển thị thông tin
        final TextView textView = binding.textNotifications;

        // Lấy token từ SharedPreferences
        String token = getTokenFromSharedPreferences();

        // Kiểm tra và hiển thị token
        if (token != null) {
            textView.setText("Token: " + token);  // Hiển thị token
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
