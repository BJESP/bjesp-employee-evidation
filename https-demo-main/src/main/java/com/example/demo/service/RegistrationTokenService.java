package com.example.demo.service;

import com.example.demo.model.RegistrationToken;
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
public class RegistrationTokenService {
    private static final BytesKeyGenerator DEFAULT_TOKEN_GENERATOR = KeyGenerators.secureRandom(15);
    private static final Charset US_ASCII = Charset.forName("US-ASCII");

    @Value("${jdj.secure.token.validity}")
    private int tokenValidityInSeconds;

    @Autowired
    RegistrationTokenRepo registrationTokenRepository;

    public RegistrationToken createSecureToken(){
        String tokenValue = new String(Base64.encodeBase64URLSafe(DEFAULT_TOKEN_GENERATOR.generateKey()), US_ASCII); // this is a sample, you can adapt as per your security need
        RegistrationToken secureToken = new RegistrationToken();
        secureToken.setToken(tokenValue);
        secureToken.setExpireAt(LocalDateTime.now().plusSeconds(getTokenValidityInSeconds()));
        this.saveSecureToken(secureToken);
        return secureToken;
    }

    public void saveSecureToken(RegistrationToken token) {
        registrationTokenRepository.save(token);
    }

    public RegistrationToken findByToken(String token) {
        return registrationTokenRepository.findByToken(token);
    }

    //Kad se iskoristi da se obrise
    public void removeToken(RegistrationToken token) {
        registrationTokenRepository.delete(token);
    }

    public void removeTokenByToken(String token) {
        registrationTokenRepository.removeByToken(token);
    }

    public int getTokenValidityInSeconds() {
        return tokenValidityInSeconds;
    }
}
