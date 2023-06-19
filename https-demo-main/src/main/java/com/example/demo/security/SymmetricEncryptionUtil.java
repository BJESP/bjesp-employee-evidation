package com.example.demo.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.Base64;


public class SymmetricEncryptionUtil {
    private static final String AES_ALGORITHM = "AES";
    @Value("${ks.filename}")
    private static String KEYSTORE_FILE;
    @Value("${ks.alias}")
    private static String KEY_ALIAS;
    @Value("${ks.password}")
    private static String KEYSTORE_PASSWORD;

    // Generisanje tajnog ključa (simetrično)
    public static SecretKey generateSecretKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(AES_ALGORITHM);
        SecureRandom secureRandom = new SecureRandom();
        keyGenerator.init(256, secureRandom);
        return keyGenerator.generateKey();
    }

    // Šifrovanje podataka
    public static String encryptData(String data, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    // Dešifrovanje podataka
    public static String decryptData(String encryptedData, SecretKey secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    public static void saveKeyToKeyStore(SecretKey secretKey) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(new FileInputStream(KEYSTORE_FILE), KEYSTORE_PASSWORD.toCharArray());

        KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
        KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(KEYSTORE_PASSWORD.toCharArray());
        keyStore.setEntry(KEY_ALIAS, secretKeyEntry, protectionParameter);

        keyStore.store(new FileOutputStream(KEYSTORE_FILE), KEYSTORE_PASSWORD.toCharArray());
    }

    // Učitavanje ključa iz keystore-a
    public static SecretKey loadKeyFromKeyStore(KeyStoreConfig keyStoreConfig) throws Exception {
        KeyStore keyStore = KeyStore.getInstance("JCEKS");
        keyStore.load(new FileInputStream(keyStoreConfig.getFileName()), keyStoreConfig.getKsPassword().toCharArray());

        KeyStore.ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(keyStoreConfig.getKsPassword().toCharArray());
        KeyStore.SecretKeyEntry secretKeyEntry = (KeyStore.SecretKeyEntry) keyStore.getEntry(keyStoreConfig.getAlias(), protectionParameter);

        return secretKeyEntry.getSecretKey();
    }

}
