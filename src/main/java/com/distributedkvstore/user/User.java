package com.distributedkvstore.user;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

import com.distributedkvstore.helper.UserInfoCRUDoperations;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;


@JsonSerialize
@JsonDeserialize
@Slf4j
public class User {

    @JsonProperty("username")
    @Getter
    @Setter
    private String username;
    @JsonProperty("password")
    @Getter
    @Setter
    private String password;
    @JsonProperty("userId")
    @Getter
    @Setter
    private String userId;

    // object mapper parameter for serialization and deserialization of user objects.
    private static final ObjectMapper objectMapper = new ObjectMapper();
    // 32 bytes = 256 bits
    private static final int HASH_LENGTH = 32;
    // Number of iterations
    private static final int ITERATIONS = 100_000;
    private static final String SALT = "grg453252rtrfeda";


    //Constructor to assign value to the new object
    public User(String username, String password, String userId) {
        this.username = username;
        this.password = password;
        this.userId = userId;
    }

    //Constructor to create a new object with username and password.
    public User(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        this.username = username;
        this.password = hashPassword(password);
        this.userId = generateUserId();
    }

    // Hash the password with PBKDF2
    public static String hashPassword(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(
                password.toCharArray(),
                Base64.getEncoder().encodeToString(SALT.getBytes()).getBytes(), // No salt used
                ITERATIONS,
                HASH_LENGTH * 8 // Key length in bits
        );
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] hash = factory.generateSecret(spec).getEncoded();
        return Base64.getEncoder().encodeToString(hash);
    }

    //Method to generate an alphanumeric id that is 6 digits long.
    private static String generateUserId() {
        SecureRandom random = new SecureRandom();
        StringBuilder userIdBuilder = new StringBuilder();
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(alphanumeric.length());
            userIdBuilder.append(alphanumeric.charAt(index));
        }

        return userIdBuilder.toString();
    }

    //Method to serialize the user object
    public static String serialize(User user) throws JsonProcessingException {
        log.info("serialize user {}", user);
        return objectMapper.writeValueAsString(user);
    }

    // Method to deserialize JSON string to user object
    public static User deserialize(String json) throws JsonProcessingException {
        JsonNode jsonNode = objectMapper.readTree(json);
        log.info("deserialize user {}", jsonNode.get("username").asText());
        return new User(jsonNode.get("username").asText(),
                        jsonNode.get("password").asText(),
                        jsonNode.get("userId").asText());
    }

    // Method to retrieve the user object when username is provided.
    public static User getUserObject(String username) throws JsonProcessingException {
        User userObj = UserInfoCRUDoperations.getOperation(username);
        return userObj;
    }

    // Method to authenticate user.
    public static User authenticate(String username, String password) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {

        User user = getUserObject(username);
        if (user!=null && user.password.equals(hashPassword(password))){
            log.info("authenticate user {}", user);
            return user;
        }
        return null;
    }

    // Method to sign up a new user.
    public static User SignUpUser(String username, String password) throws JsonProcessingException, NoSuchAlgorithmException, InvalidKeySpecException {
        User newUser = new User(username, password);
        UserInfoCRUDoperations.postOperation(username, User.serialize(newUser));
        log.info("Signed up new user {}", newUser);
        return newUser;
    }
}

