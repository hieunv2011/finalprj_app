package com.example.finalproject.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONObject;

public class MqttHandler {

    private MqttClient client;
    private MqttMessageListener listener;
    private Context context;

    public MqttHandler(Context context) {
        this.context = context;
    }

    public void connect(String brokerUrl, String clientId, String username, String password) {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            client = new MqttClient(brokerUrl, clientId, persistence);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            connectOptions.setUserName(username);
            connectOptions.setPassword(password.toCharArray());
            client.connect(connectOptions);

            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // Xử lý khi mất kết nối
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    try {
                        // Lấy deviceId từ SharedPreferences
                        String deviceId = getDeviceIdFromSharedPreferences();

                        // Kiểm tra nếu topic chứa deviceId của thiết bị hiện tại
//                        if (topic.equals("response/device004")) {
                            String receivedMessage = new String(message.getPayload());
                            Log.d("MqttHandler", "Received JSON Object: " + message.getPayload());
                            JSONObject jsonObject = new JSONObject(receivedMessage);
                            jsonObject.put("deviceId", deviceId); // Thêm deviceId vào dữ liệu nhận được
                            Log.d("MqttHandler", "Received JSON Object: " + jsonObject);
                            if (listener != null) {
                                listener.onMessageReceived(jsonObject); // Gửi thông báo cho listener

                            }
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void deliveryComplete(org.eclipse.paho.client.mqttv3.IMqttDeliveryToken token) {
                    // Xử lý khi gửi tin nhắn hoàn thành
                }
            });

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    // Phương thức để lấy deviceId từ SharedPreferences
    private String getDeviceIdFromSharedPreferences() {
        SharedPreferences sharedPreferences = context.getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString("deviceId", "defaultDeviceId"); // Trả về deviceId hoặc giá trị mặc định
    }

//    public void subscribe(String topic) {
//        try {
//            client.subscribe(topic);
//        } catch (MqttException e) {
//            e.printStackTrace();
//        }
//    }
public void subscribe(String topic) {
    try {
        if (client != null && client.isConnected()) {
            client.subscribe(topic, 1); // QoS 1 hoặc 0 tùy nhu cầu
            Log.d("MqttHandler", "Subscribed successfully to topic: " + topic);
        } else {
            Log.e("MqttHandler", "Failed to subscribe. MQTT client is not connected.");
        }
    } catch (MqttException e) {
//        Log.e("MqttHandler", "Error subscribing to topic: " + topic, e);
    }
}


    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, String message) {
        try {
            MqttMessage mqttMessage = new MqttMessage(message.getBytes());
            mqttMessage.setQos(1);
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void setMqttMessageListener(MqttMessageListener listener) {
        this.listener = listener;
    }


    public interface MqttMessageListener {
        void onMessageReceived(JSONObject message);
    }
}
