package com.example.demo.security;

import javax.crypto.SecretKey;
import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class StringAttributeConverter implements AttributeConverter<String, String> {

    private final KeyStoreConfig keyStoreConfig;

    public StringAttributeConverter(KeyStoreConfig keyStoreConfig) {
        this.keyStoreConfig = keyStoreConfig;
    }

    @Override
    public String convertToDatabaseColumn(String attribute) {
//        SecretKey secretKey = null;
//        try {
//            secretKey = SymmetricEncryptionUtil.generateSecretKey();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        try {
//            SymmetricEncryptionUtil.saveKeyToKeyStore(secretKey);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
        SecretKey loadedKey = null;
        // Učitavanje ključa iz keystore-a
        try {
            loadedKey = SymmetricEncryptionUtil.loadKeyFromKeyStore(keyStoreConfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String encryptedValue = null;
        try {
            encryptedValue = SymmetricEncryptionUtil.encryptData(attribute, loadedKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return encryptedValue;
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        // Implementacija logike za dešifrovanje atributa
        SecretKey loadedKey = null;
        // Učitavanje ključa iz keystore-a
        try {
            loadedKey = SymmetricEncryptionUtil.loadKeyFromKeyStore(keyStoreConfig);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        String decryptedValue = null;
        try {
            decryptedValue = SymmetricEncryptionUtil.decryptData(dbData, loadedKey);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return decryptedValue;
    }
}
