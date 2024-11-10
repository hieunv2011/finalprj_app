package com.example.finalproject.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UserResponse {

    @SerializedName("username")
    private String username;

    @SerializedName("email")
    private String email;

    @SerializedName("contact")
    private Contact contact;

    @SerializedName("devices")
    private List<Device> devices;

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Getter for email
    public String getEmail() {
        return email;
    }

    // Getter for contact
    public Contact getContact() {
        return contact;
    }

    // Getter for devices
    public List<Device> getDevices() {
        return devices;
    }

    // Contact class
    public static class Contact {
        @SerializedName("email")
        private String email;

        @SerializedName("address")
        private String address;

        @SerializedName("building")
        private String building;

        @SerializedName("emergencycontact")
        private String emergencyContact;

        @SerializedName("coordinates")
        private Coordinates coordinates;

        // Getters for Contact fields
        public String getEmail() {
            return email;
        }

        public String getAddress() {
            return address;
        }

        public String getBuilding() {
            return building;
        }

        public String getEmergencyContact() {
            return emergencyContact;
        }

        public Coordinates getCoordinates() {
            return coordinates;
        }

        // Coordinates class
        public static class Coordinates {
            @SerializedName("lat")
            private double lat;

            @SerializedName("lng")
            private double lng;

            // Getters for Coordinates
            public double getLat() {
                return lat;
            }

            public double getLng() {
                return lng;
            }
        }
    }

    // Device class
    public static class Device {
        @SerializedName("deviceId")
        private String deviceId;

        // Getter for deviceId
        public String getDeviceId() {
            return deviceId;
        }
    }
}
