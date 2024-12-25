package com.example.finalproject.ui.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
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

        // Lấy đối tượng thiết bị
        UserDevicesResponse.Device device = devices.get(position);

        // Tham chiếu các view
        TextView deviceName = convertView.findViewById(R.id.device_name);
        TextView deviceLocation = convertView.findViewById(R.id.device_location);
        TextView deviceId = convertView.findViewById(R.id.device_id);
        ImageView deviceStatusIcon = convertView.findViewById(R.id.device_status_icon);

        // Gán dữ liệu
        deviceName.setText(device.getName());
        deviceLocation.setText("Vị trí thiết bị: " + device.getLocation());
        deviceId.setText("ID thiết bị: " + device.getDeviceId());

        // Gán trạng thái
        boolean isActive = "active".equals(device.getStatus());
        deviceStatusIcon.setImageResource(isActive ? R.drawable.ic_active : R.drawable.ic_inactive);

        return convertView;
    }
}
