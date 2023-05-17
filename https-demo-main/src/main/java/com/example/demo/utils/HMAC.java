package com.example.demo.utils;

import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

@Service
public class HMAC {
    private static final String HMAC_ALGORITHM = "HmacSHA256";

    public static String generateHmac(String data, String secretKey) {
        try {
            SecretKeySpec signingKey = new SecretKeySpec(secretKey.getBytes(), HMAC_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);
            mac.init(signingKey);
            byte[] hmacBytes = mac.doFinal(data.getBytes());
            return Base64.getEncoder().encodeToString(hmacBytes);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            // Handle the exception appropriately
            throw new RuntimeException("Failed to generate HMAC: " + e.getMessage(), e);
        }
    }
    public static boolean verifyHmac(String data, String hmac, String secretKey) {
        String generatedHmac = generateHmac(data, secretKey);
        return generatedHmac.equals(hmac);
    }
}
