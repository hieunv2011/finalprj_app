package com.example.finalproject.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.api.ApiService;
import com.example.finalproject.api.UserDevicesResponse;
import com.example.finalproject.api.UserResponse;
import com.example.finalproject.api.WeatherApiService;
import com.example.finalproject.api.WeatherResponse;
import com.example.finalproject.databinding.FragmentHomeBinding;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private List<UserDevicesResponse.Device> devicesList = new ArrayList<>();
    private DeviceListAdapter devicesAdapter; // Dùng adapter đúng loại

    private String getTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // TextViews cho thông tin thời tiết
        final TextView cityNameText = binding.cityNameText;
        final TextView weatherDescriptionText = binding.weatherDescriptionText;
        final TextView temperatureText = binding.temperatureText;
        // ListView cho danh sách thiết bị

        ListView deviceListView = binding.deviceListView;

        // Khởi tạo adapter cho danh sách thiết bị
        devicesAdapter = new DeviceListAdapter(requireContext(), devicesList);
        deviceListView.setAdapter(devicesAdapter);

        // Hiển thị thông tin thời tiết
        displayWeatherInfo(cityNameText, weatherDescriptionText, temperatureText);

        // Lấy thông tin người dùng và danh sách thiết bị
        String token = getTokenFromSharedPreferences();
        if (token != null) {
            fetchUserProfile(token);
        } else {
            cityNameText.setText("Token not found.");
        }

        return root;
    }

    private void displayWeatherInfo(TextView cityNameText, TextView weatherDescriptionText, TextView temperatureText) {
        double lat = 21.005245324082434;
        double lon = 105.84155554692785;
        String apiKey = "16bd87cd65f7574f7ebba2759426942a";

        WeatherApiService.retrofit.create(WeatherApiService.class)
                .getWeather(lat, lon, apiKey)
                .enqueue(new Callback<WeatherResponse>() {
                    @Override
                    public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            WeatherResponse weatherResponse = response.body();
                            String cityName = weatherResponse.getName();
                            String description = weatherResponse.getWeather().get(0).getDescription();
                            double tempK = weatherResponse.getMain().getTemp();
                            double tempC = tempK - 273.15;

                            cityNameText.setText(cityName);
                            weatherDescriptionText.setText("Weather: " + description);
                            temperatureText.setText(String.format("%.2f", tempC) + "°C");
                        } else {
                            cityNameText.setText("Failed to fetch weather data.");
                            weatherDescriptionText.setText("Error");
                            temperatureText.setText("Error");
                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherResponse> call, Throwable t) {
                        cityNameText.setText("Error: " + t.getMessage());
                        weatherDescriptionText.setText("Error");
                        temperatureText.setText("Error");
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
                // Không làm gì khi thất bại
            }
        });
    }

    private void fetchUserDevices(String token, String userId) {
        ApiService.apiService.getUserDevices("Bearer " + token, userId).enqueue(new Callback<UserDevicesResponse>() {
            @Override
            public void onResponse(Call<UserDevicesResponse> call, Response<UserDevicesResponse> response) {
                final TextView deviceQuan = binding.deviceQuan;
                ProgressBar loadingProgressBar = binding.loadingProgressBar;  // Lấy ProgressBar

                // Ẩn ProgressBar khi dữ liệu đã tải xong
                loadingProgressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null) {
                    UserDevicesResponse userDevicesResponse = response.body();
                    if (userDevicesResponse.getDevices() != null && !userDevicesResponse.getDevices().isEmpty()) {
                        devicesList.clear();
                        devicesList.addAll(userDevicesResponse.getDevices());

                        // Cập nhật adapter với danh sách thiết bị
                        devicesAdapter.notifyDataSetChanged();

                        deviceQuan.setText("Số lượng thiết bị: " + devicesList.size());
                    } else {
                        deviceQuan.setText("Số lượng thiết bị: 0");
                    }
                }
            }

            @Override
            public void onFailure(Call<UserDevicesResponse> call, Throwable t) {
                // Ẩn ProgressBar khi có lỗi
                ProgressBar loadingProgressBar = binding.loadingProgressBar;
                loadingProgressBar.setVisibility(View.GONE);

                // Xử lý lỗi nếu cần
            }
        });

        // Hiển thị ProgressBar khi bắt đầu tải dữ liệu
        ProgressBar loadingProgressBar = binding.loadingProgressBar;
        loadingProgressBar.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

