package com.example.finalproject.ui.dashboard;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.util.GeoPoint;

import com.example.finalproject.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private MapView mapView;
    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        mapView = binding.mapView;

        Configuration.getInstance().load(getActivity().getApplicationContext(),
                androidx.preference.PreferenceManager.getDefaultSharedPreferences(getActivity()));
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);

        // Tạo các điểm GeoPoint cho các thiết bị
        GeoPoint device1 = new GeoPoint(21.004234299719624, 105.84485626775297); // Device01
        GeoPoint device2 = new GeoPoint(21.010234299719624, 105.85085626775297); // Device02
        GeoPoint device3 = new GeoPoint(21.014234299719624, 105.86085626775297); // Device03

        // Thiết lập các marker cho từng điểm
        Marker marker1 = new Marker(mapView);
        marker1.setPosition(device1);
        marker1.setTitle("Device01");
        mapView.getOverlays().add(marker1);

        Marker marker2 = new Marker(mapView);
        marker2.setPosition(device2);
        marker2.setTitle("Device02");
        mapView.getOverlays().add(marker2);

        Marker marker3 = new Marker(mapView);
        marker3.setPosition(device3);
        marker3.setTitle("Device03");
        mapView.getOverlays().add(marker3);

        // Thiết lập mức độ zoom và vị trí trung tâm
        mapView.getController().setZoom(10); // Thiết lập mức độ zoom
        mapView.getController().setCenter(device1); // Thiết lập vị trí trung tâm ban đầu (device1)

        // Xử lý sự kiện khi ấn vào marker
        marker1.setOnMarkerClickListener((marker, mapView) -> {
            // Khi ấn vào marker1, có thể hiển thị thông tin thiết bị
            // Ví dụ: "Device01"
            return false; // Trả về false để tiếp tục hiển thị InfoWindow mặc định
        });

        marker2.setOnMarkerClickListener((marker, mapView) -> {
            // Khi ấn vào marker2
            return false;
        });

        marker3.setOnMarkerClickListener((marker, mapView) -> {
            // Khi ấn vào marker3
            return false;
        });

        requestPermissionsIfNecessary(new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        });

        return root;
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
}
