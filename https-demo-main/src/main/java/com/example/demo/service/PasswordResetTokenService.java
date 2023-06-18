package com.example.demo.service;

import com.example.demo.model.PasswordResetToken;
import com.example.demo.model.RegistrationToken;
import com.example.demo.repo.PasswordResetTokenRepo;
import com.example.demo.repo.RegistrationTokenRepo;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.keygen.BytesKeyGenerator;
import org.springframework.security.crypto.keygen.KeyGenerators;
import org.springframework.stereotype.Service;

import java.nio.charset.Charset;
import java.time.LocalDateTime;

@Service
public class PasswordResetTokenService {
    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);
    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    @Value("3600")
    private int tokenValidityInSeconds;
    @Autowired
    PasswordResetTokenRepo passwordResetTokenRepository;


    public PasswordResetToken createSecureToken(){
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), US_ASCII); // this is a sample, you can adapt as per your security need
        PasswordResetToken secureToken = new PasswordResetToken();
        secureToken.setToken(tokenValue);
        secureToken.setExpireAt(LocalDateTime.now().plusSeconds(getTokenValidityInSeconds()));
        this.saveSecureToken(secureToken);
        return secureToken;
    }

    public void saveSecureToken(PasswordResetToken token) {
        passwordResetTokenRepository.save(token);
    }

    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    public void removeToken(PasswordResetToken token) {
        passwordResetTokenRepository.delete(token);
    }

    public void removeTokenByToken(String token) {
        passwordResetTokenRepository.removeByToken(token);
    }

    public int getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }
}
