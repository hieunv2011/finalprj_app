package com.example.finalproject.ui.dashboard;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;

import com.example.finalproject.R;
import com.example.finalproject.databinding.FragmentDashboardBinding;
import com.example.finalproject.services.MqttHandler;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    private FragmentDashboardBinding binding;
    private MqttHandler mqttHandler;
    private Spinner idSpinner;

    private String getTokenFromSharedPreferences() {
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        return sharedPreferences.getString("token", null);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = binding.mapView;
        mqttHandler = new MqttHandler(getActivity());  // Truyền context vào MqttHandler
        mqttHandler.connect("tcp://103.1.238.175:1883", "android133", "test", "testadmin");

        mqttHandler.setMqttMessageListener(this::updateReceivedMessage);

        idSpinner= binding.idSpinner;
        String token = getTokenFromSharedPreferences();
        if (token != null) {
            fetchUserDevicesFromSharedPreferences(); // Fetch device details and subscribe to topics
        } else {
            // Handle case where token is null
        }

        Configuration.getInstance().load(getActivity().getApplicationContext(),
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(getActivity()));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Tọa độ của Hà Nội, Việt Nam
        GeoPoint hanoi = new GeoPoint(21.0285, 105.8542);
        mapView.getController().setCenter(hanoi);  // Đặt điểm trung tâm là Hà Nội
        mapView.getController().setZoom(9);  // Thiết lập mức zoom

        requestPermissionsIfNecessary(new String[] {
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        return root;
    }

    private void fetchUserDevicesFromSharedPreferences() {
        // Giả sử dữ liệu thiết bị (latitude, longitude) đã được lưu trong SharedPreferences
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("user_prefs", getContext().MODE_PRIVATE);
        String devicesJson = sharedPreferences.getString("user_devices", ""); // Lấy dữ liệu thiết bị dưới dạng JSON
//        Log.d("SharedPreferences", "UserId: " + sharedPreferences.getString("user_id", "No Id"));
//        Log.d("SharedPreferences", "Token: " + sharedPreferences.getString("token", "No Token"));

        if (!devicesJson.isEmpty()) {
            try {
                JSONArray devicesArray = new JSONArray(devicesJson);
                ArrayList<String> deviceIds = new ArrayList<>();
                // Xóa tất cả các marker hiện tại trên bản đồ
                mapView.getOverlays().clear();

                for (int i = 0; i < devicesArray.length(); i++) {
                    JSONObject device = devicesArray.getJSONObject(i);
                    String deviceId = device.getString("deviceId");
                    deviceIds.add(deviceId);
                    double latitude = device.getDouble("latitude");
                    double longitude = device.getDouble("longitude");
                    GeoPoint deviceLocation = new GeoPoint(latitude, longitude);

                    // Tạo marker mới cho mỗi thiết bị
                    Marker marker = new Marker(mapView);
                    marker.setPosition(deviceLocation);
                    marker.setTitle(deviceId);  // Đặt tên thiết bị hoặc thông tin khác
                    mapView.getOverlays().add(marker);

                    marker.setOnMarkerClickListener((clickedMarker, mapView) -> {
                        String name = device.optString("name", "Unknown Device");  // Kiểm tra và lấy tên thiết bị
                        clickedMarker.setSnippet(name);  // Hiển thị tên thiết bị
                        clickedMarker.showInfoWindow();
                        return false;
                    });



                    //Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                            R.layout.spinner_item,
//                            android.R.layout.simple_spinner_item,
                            deviceIds);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    idSpinner.setAdapter(adapter);

                    idSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(android.widget.AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            String selectedDeviceId = parentView.getItemAtPosition(position).toString();
                            resetTextViewMessages();
                            // Đăng ký vào topic "response/deviceId"
                            mqttHandler.subscribe("nvhresponse/" + selectedDeviceId);
                            Log.d("SpinnerSelection", "Selected Device ID: " + selectedDeviceId);  // Log ra Device ID được chọn
                        }

                        @Override
                        public void onNothingSelected(android.widget.AdapterView<?> parentView) {

                        }
                    });

                }
            } catch (Exception e) {
                Log.e("DashboardFragment", "Error parsing devices JSON", e);
            }
        } else {
            Log.d("DeviceLocation", "Không có thiết bị nào trong SharedPreferences");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                // Handle permission denial
            }
        }
    }

    private void requestPermissionsIfNecessary(String[] permissions) {
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(getActivity(), permission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_PERMISSIONS_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void updateReceivedMessage(JSONObject jsonObject) {
        final TextView textGas = binding.textGas;
        final TextView textFlame = binding.textFlame;
        final TextView textTemp = binding.textTemp;
        final TextView textHum = binding.textHum;
        requireActivity().runOnUiThread(() -> {
            try {
                String gasData = jsonObject.getString("gas_ppm");
                String flameData = jsonObject.getString("flame_detected");
                String tempData = jsonObject.getString("temperature");
                String humData = jsonObject.getString("humidity");
                String dustData = jsonObject.getString("dust_density");
                textGas.setText("Gas PPM: " + gasData);
                textFlame.setText("Flame Detected: " + flameData);
                textTemp.setText("Temperature: " + tempData);
                textHum.setText("Humidity: " + humData);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void resetTextViewMessages() {
        final TextView textGas = binding.textGas;
        final TextView textFlame = binding.textFlame;
        final TextView textTemp = binding.textTemp;
        final TextView textHum = binding.textHum;

        // Reset lại nội dung TextView về giá trị mặc định hoặc rỗng
        textGas.setText("Khí ga: Chưa có dữ liệu");
        textFlame.setText("Nhận diện lửa: Chưa có dữ liệu");
        textTemp.setText("Nhiệt độ: Chưa có dữ liệu");
        textHum.setText("Độ ẩm: Chưa có dữ liệu");
    }


}


