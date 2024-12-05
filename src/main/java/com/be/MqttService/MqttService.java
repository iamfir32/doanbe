package com.be.MqttService;

import com.be.Const.UserRole;
import com.be.Entity.Device;
import com.be.Entity.Team;
import com.be.Entity.User;
import com.be.Repository.DeviceRepository;
import com.be.Repository.UserRepository;
import com.be.WebsocketService.WebSocketService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
public class MqttService {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private DeviceRepository deviceRepository;

    @Autowired
    private CacheManager cacheManager;


    private String broker = "tcp://localhost:1883";
    private String clientId = "mqtt-java-client";
    private String topic = "sos";

    private byte[] key = new byte[] {
            (byte) 0x6A, (byte) 0xE8, (byte) 0x9E, (byte) 0x8B,
            (byte) 0xB5, (byte) 0xC7, (byte) 0x2B, (byte) 0x8E,
            (byte) 0x03, (byte) 0xC6, (byte) 0xFD, (byte) 0x0B,
            (byte) 0x1A, (byte) 0x57, (byte) 0xCF, (byte) 0xAC,
            (byte) 0x07, (byte) 0xAE, (byte) 0xF5, (byte) 0xA7,
            (byte) 0x38, (byte) 0xA3, (byte) 0x23, (byte) 0x2B,
            (byte) 0x32, (byte) 0x5C, (byte) 0xFE, (byte) 0x92,
            (byte) 0x21, (byte) 0x77, (byte) 0xC5, (byte) 0x65
    };

    private byte[] iv = new byte[] {
            (byte) 0x1F, (byte) 0xDC, (byte) 0x15, (byte) 0x25,
            (byte) 0x74, (byte) 0x1A, (byte) 0x37, (byte) 0x06,
            (byte) 0xD8, (byte) 0x94, (byte) 0xD6, (byte) 0x49,
            (byte) 0xB3, (byte) 0xC1, (byte) 0x93, (byte) 0x5C
    };

    public MqttService() {
        try {
            MqttClient mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("Connection lost! " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8);
                    String decryptedPayload = decryptMessage(payload);

                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(decryptedPayload);

                    Long userId = jsonNode.get("id").asLong();

                    Device user = getCachedUser(userId);

                    if (user == null) {
                        System.out.println("User not found for ID: " + userId);
                        return;
                    }
                    Team team = new Team();
                    if(user.getTeamRescue()!=null){
                        team=user.getTeamRescue();
                    } else if(user.getTeamVictim()!=null){
                        team=user.getTeamVictim();
                    }

                    ((ObjectNode) jsonNode).put("username", user.getUsername()+"-"+team.getName());
                    ((ObjectNode) jsonNode).put("age", user.getAge());
                    ((ObjectNode) jsonNode).put("avatar", user.getAvatar());
                    ((ObjectNode) jsonNode).put("role", user.getRole().name());
                    ((ObjectNode) jsonNode).put("color", team.getColor());

                    String updatedPayload = objectMapper.writeValueAsString(jsonNode);
                    if(team.getId()!=null){
                        simpMessagingTemplate.convertAndSendToUser("1", "/team"+team.getId(), updatedPayload);
                    }
                    simpMessagingTemplate.convertAndSendToUser("1", "/admin", updatedPayload);

                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                }
            });

            mqttClient.connect(connOpts);
            mqttClient.subscribe(topic,1);
            System.out.println("Subscribed to topic: " + topic);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String decryptMessage(String encryptedMessage) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);

        byte[] decodedMessage = Base64.getDecoder().decode(encryptedMessage);
        byte[] decryptedBytes = cipher.doFinal(decodedMessage);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }

    @Cacheable("users")
    public Device getCachedUser(Long userId) {
        Optional<Device> userOptional = deviceRepository.findById(userId);
        return userOptional.orElse(null);
    }
}
