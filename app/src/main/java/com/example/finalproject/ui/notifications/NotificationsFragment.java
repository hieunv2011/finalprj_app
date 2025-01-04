package com.example.finalproject.ui.notifications;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.LoginActivity;
import com.example.finalproject.R;
import com.example.finalproject.api.ApiService;
import com.example.finalproject.api.UserProfileRequest;
import com.example.finalproject.api.UserResponse;
import com.example.finalproject.databinding.FragmentNotificationsBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.example.finalproject.utils.Ultis;
import com.google.gson.Gson;

import java.util.List;

public class NotificationsFragment extends Fragment {
    private FragmentNotificationsBinding binding;
    private ProgressBar progressBar; // Declare the ProgressBar

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Initialize the ProgressBar
        progressBar = root.findViewById(R.id.progressBar); // Find the ProgressBar by ID
        progressBar.setVisibility(View.GONE); // Initially hidden

        final TextView textUsername = binding.textUsername;
        final TextView textEmail = binding.textEmail;
        final TextView textContactInfo = binding.textContactInfo;
        final TextView textDevices = binding.textDevices;
        final TextView textphone = binding.textPhone;
        Button buttonLogout = binding.buttonLogout;
        Button btnToken = binding.btnToken;
        Button btnEdit =binding.btnEdit;

        String token = getTokenFromSharedPreferences();

        if (token != null) {
            // Show ProgressBar while loading data
            progressBar.setVisibility(View.VISIBLE);

            ApiService.apiService.getUserProfile("Bearer " + token).enqueue(new Callback<UserResponse>() {
                @Override
                public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                    // Hide ProgressBar when data is loaded
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        UserResponse user = response.body();

                        textUsername.setText(user.getUsername());
                        textEmail.setText(user.getEmail());
                        textphone.setText(user.getPhone());

                        String contactInfo = "Địa chỉ: " + user.getContact().getAddress();
                        textContactInfo.setText(contactInfo);

                        StringBuilder devicesInfo = new StringBuilder();
                        List<UserResponse.Device> devices = user.getDevices();
                        for (int i = 0; i < devices.size(); i++) {
                            String deviceId = devices.get(i).getDeviceId().replaceAll("[^0-9]", "");
                            devicesInfo.append("device").append(String.format("%03d", Integer.parseInt(deviceId)));

                            if (i < devices.size() - 1) {
                                devicesInfo.append(",");
                            }
                        }
                        devicesInfo.append(".");
                        textDevices.setText(devicesInfo.toString());
                        btnEdit.setOnClickListener(v -> {
                            LayoutInflater dialogInflater = LayoutInflater.from(getContext());
                            View dialogView = dialogInflater.inflate(R.layout.dialog_edit, null);

                            // Khởi tạo đối tượng UserProfileRequest để cập nhật thông tin
                            UserProfileRequest userProfileRequest = new UserProfileRequest();
                            UserProfileRequest.Contact contact = new UserProfileRequest.Contact();

                            // Đảm bảo các thông tin email, username, phone từ user được thêm vào
                            userProfileRequest.setEmail(user.getEmail());
                            userProfileRequest.setUsername(user.getUsername());
                            userProfileRequest.setPhone(user.getPhone());

                            // Lấy thông tin contact từ user và thiết lập lại vào userProfileRequest
                            contact.setAddress(user.getContact().getAddress());
                            contact.setBuilding(user.getContact().getBuilding());
                            contact.setEmergencyContact(user.getContact().getEmergencyContact());
                            contact.setEmail(user.getContact().getEmail());

                            // Thiết lập coordinates (lat, lng)
                            UserProfileRequest.Contact.Coordinates coordinates = new UserProfileRequest.Contact.Coordinates();
//                            coordinates.setLat(user.getContact().getCoordinates().getLat());
//                            coordinates.setLng(user.getContact().getCoordinates().getLng());

                            contact.setCoordinates(coordinates); // Thêm coordinates vào contact
                            userProfileRequest.setContact(contact);

                            // Lấy các EditText trong dialog để điền dữ liệu
                            EditText editEmail = dialogView.findViewById(R.id.text_edit_email);
                            EditText editAddress = dialogView.findViewById(R.id.text_edit_address);
                            EditText editPhone = dialogView.findViewById(R.id.text_edit_phone);

                            // Điền thông tin hiện tại vào các EditText
                            editEmail.setText(userProfileRequest.getEmail());
                            editAddress.setText(userProfileRequest.getContact().getAddress());
                            editPhone.setText(userProfileRequest.getPhone());

                            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                            builder.setView(dialogView)
                                    .setCancelable(false)
                                    .setTitle("Chỉnh sửa thông tin");

                            AlertDialog dialog = builder.create();
                            dialog.getWindow().setBackgroundDrawableResource(R.drawable.rounded_dialog_bg);
                            dialog.show();

                            // Thêm hành động cho nút Hủy trong dialog
                            Button btnCancel = dialogView.findViewById(R.id.button_cancel);
                            btnCancel.setOnClickListener(v1 -> {
                                dialog.dismiss(); // Đóng dialog khi ấn nút Hủy
                            });

                            // Thêm hành động cho nút Lưu trong dialog
                            Button btnSave = dialogView.findViewById(R.id.button_save);
                            btnSave.setOnClickListener(v1 -> {
                                // Lấy dữ liệu từ các EditText và cập nhật lại đối tượng UserProfileRequest
                                userProfileRequest.setEmail(editEmail.getText().toString());
                                userProfileRequest.getContact().setAddress(editAddress.getText().toString());
                                userProfileRequest.setPhone(editPhone.getText().toString());

                                // Chuyển đối tượng userProfileRequest thành JSON (nếu cần thiết)
                                Gson gson = new Gson();
                                String json = gson.toJson(userProfileRequest);

                                // Log dữ liệu đã thay đổi
                                Log.d(Ultis.TAG, "Updated UserProfile JSON: " + json);

                                // Lấy token và userId từ SharedPreferences
                                SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
                                String token = sharedPreferences.getString("token", "");  // Lấy token từ SharedPreferences
                                String userId = sharedPreferences.getString("user_id", "");    // Lấy userId từ SharedPreferences

                                // Kiểm tra nếu token hoặc userId rỗng, không thực hiện gọi API
                                if (token.isEmpty() || userId.isEmpty()) {
                                    Log.d(Ultis.TAG, "Token hoặc UserId không hợp lệ");
                                    return; // Dừng lại nếu không có token hoặc userId
                                }

                                // Gọi phương thức updateUserProfile trong ApiService
                                ApiService.apiService.updateUserProfile("Bearer " + token, userId, userProfileRequest)
                                        .enqueue(new Callback<UserResponse>() {
                                            @Override
                                            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                                if (response.isSuccessful()) {
                                                    // Cập nhật thành công
                                                    Log.d(Ultis.TAG, "Cập nhật thành công: " + response.body());

                                                    // Sau khi lưu, gọi lại API để tải lại thông tin người dùng và hiển thị ProgressBar
                                                    progressBar.setVisibility(View.VISIBLE);

                                                    ApiService.apiService.getUserProfile("Bearer " + token).enqueue(new Callback<UserResponse>() {
                                                        @Override
                                                        public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                                                            progressBar.setVisibility(View.GONE); // Ẩn ProgressBar khi dữ liệu được tải

                                                            if (response.isSuccessful() && response.body() != null) {
                                                                UserResponse user = response.body();

                                                                // Cập nhật lại giao diện với dữ liệu mới
                                                                textUsername.setText(user.getUsername());
                                                                textEmail.setText(user.getEmail());
                                                                textphone.setText(user.getPhone());

                                                                String contactInfo = "Địa chỉ: " + user.getContact().getAddress();
                                                                textContactInfo.setText(contactInfo);

                                                                StringBuilder devicesInfo = new StringBuilder();
                                                                List<UserResponse.Device> devices = user.getDevices();
                                                                for (int i = 0; i < devices.size(); i++) {
                                                                    String deviceId = devices.get(i).getDeviceId().replaceAll("[^0-9]", "");
                                                                    devicesInfo.append("device").append(String.format("%03d", Integer.parseInt(deviceId)));

                                                                    if (i < devices.size() - 1) {
                                                                        devicesInfo.append(",");
                                                                    }
                                                                }
                                                                devicesInfo.append(".");
                                                                textDevices.setText(devicesInfo.toString());
                                                            } else {
                                                                textContactInfo.setText("Failed to fetch user data.");
                                                            }
                                                        }

                                                        @Override
                                                        public void onFailure(Call<UserResponse> call, Throwable t) {
                                                            progressBar.setVisibility(View.GONE); // Ẩn ProgressBar nếu có lỗi
                                                            textContactInfo.setText("Error: " + t.getMessage());
                                                        }
                                                    });

                                                } else {
                                                    // Xử lý lỗi khi không cập nhật thành công
                                                    Log.d(Ultis.TAG, "Cập nhật thất bại: " + response.message());
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<UserResponse> call, Throwable t) {
                                                // Xử lý lỗi khi gọi API thất bại
                                                Log.d(Ultis.TAG, "Lỗi khi gọi API: " + t.getMessage());
                                            }
                                        });

                                // Đóng dialog sau khi lưu thông tin
                                dialog.dismiss();
                            });

                        });

                    } else {
                        textContactInfo.setText("Failed to fetch user data.");
                    }
                }

                @Override
                public void onFailure(Call<UserResponse> call, Throwable t) {
                    progressBar.setVisibility(View.GONE); // Hide ProgressBar on failure
                    textContactInfo.setText("Error: " + t.getMessage());
                }
            });
        } else {
            Toast.makeText(getContext(), "No token found.", Toast.LENGTH_SHORT).show();
        }

        buttonLogout.setOnClickListener(v -> {
            SharedPreferences preferences = requireActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.remove("token");
            editor.apply();

            Toast.makeText(getContext(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        btnToken.setOnClickListener(v -> {
            FirebaseMessaging.getInstance().getToken()
                    .addOnSuccessListener(new OnSuccessListener<String>() {
                        @Override
                        public void onSuccess(String token) {
                            Log.d(Ultis.TAG, token);
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Fail", Toast.LENGTH_LONG).show();
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
