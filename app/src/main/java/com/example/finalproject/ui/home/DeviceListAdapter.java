package com.example.finalproject.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.finalproject.R;
import com.example.finalproject.api.UserDevicesResponse;

import java.util.List;

public class DeviceListAdapter extends ArrayAdapter<UserDevicesResponse.Device> {

    private Context context;
    private List<UserDevicesResponse.Device> devices;

    public DeviceListAdapter(Context context, List<UserDevicesResponse.Device> devices) {
        super(context, R.layout.list_item_device, devices);
        this.context = context;
        this.devices = devices;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_device, parent, false);
        }

        // Lấy đối tượng thiết bị ở vị trí hiện tại
        UserDevicesResponse.Device device = devices.get(position);

        // Tìm các TextView trong layout
        TextView deviceName = convertView.findViewById(R.id.device_name);
        TextView deviceLocation = convertView.findViewById(R.id.device_location);
        TextView deviceId = convertView.findViewById(R.id.device_id);
        TextView deviceStatus = convertView.findViewById(R.id.device_status);

        // Gán dữ liệu vào các TextView
        deviceName.setText(device.getName());
        deviceLocation.setText("Location: " + device.getLocation());
        deviceId.setText("Device ID: " + device.getDeviceId());
        deviceStatus.setText("Status: " + device.getStatus());

        return convertView;
    }
}
