package com.example.finalproject.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.finalproject.api.WeatherApiService;
import com.example.finalproject.api.WeatherResponse;
import com.example.finalproject.databinding.FragmentHomeBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView cityNameText = binding.cityNameText;
        final TextView weatherDescriptionText = binding.weatherDescriptionText;
        final TextView temperatureText = binding.temperatureText;

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

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
