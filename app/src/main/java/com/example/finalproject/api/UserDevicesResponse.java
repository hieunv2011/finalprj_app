package com.example.finalproject.api;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class UserDevicesResponse {
    @SerializedName("devices")
    private List<Device> devices;

    public static class Device {
        @SerializedName("_id")
        private String id;

        @SerializedName("deviceId")
        private String deviceId;

        @SerializedName("name")
        private String name;

        @SerializedName("location")
        private String location;

        @SerializedName("status")
        private String status;

        @SerializedName("latitude")
        private double latitude;

        @SerializedName("longitude")
        private double longitude;

        @SerializedName("sensorData")
        private List<Object> sensorData;

        @SerializedName("lastChecked")
        private String lastChecked;

        @SerializedName("__v")
        private int version;

        // Getters
        public String getId() {
            return id;
        }

        public String getDeviceId() {
            return deviceId;
        }

        public String getName() {
            return name;
        }

        public String getLocation() {
            return location;
        }

        public String getStatus() {
            return status;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public List<Object> getSensorData() {
            return sensorData;
        }

        public String getLastChecked() {
            return lastChecked;
        }

        public int getVersion() {
            return version;
        }
    }

    public List<Device> getDevices() {
        return devices;
    }
}
