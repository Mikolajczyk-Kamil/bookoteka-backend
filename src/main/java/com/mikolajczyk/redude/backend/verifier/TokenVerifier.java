package com.mikolajczyk.redude.backend.verifier;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.mikolajczyk.redude.backend.domain.User;
import com.mikolajczyk.redude.backend.dto.UserDto;
import com.mikolajczyk.redude.backend.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collections;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenVerifier {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;
    private final UserMapper mapper;

    public User verify(String token) throws GeneralSecurityException, IOException {
        log.info("Verifying token...");
        GoogleIdToken idToken = null;
        try {
            idToken = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), GsonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(clientId))
                    .build().verify(token);
        } catch (Exception e) {
            log.warn("User unauthenticated");
        }
        if (idToken != null) {
            log.info("User successfully authenticated");
            GoogleIdToken.Payload payload = idToken.getPayload();
            String gId = payload.getSubject();
            String email = payload.getEmail();
            String name = (String) payload.get("given_name");
            String lastname = (String) payload.get("family_name");
            String pictureUrl = (String) payload.get("picture");
            String locale = (String) payload.get("locale");
            return mapper.mapToUser(new UserDto(gId, name, lastname, email, pictureUrl));
        }
        log.info("User verification failed");
        return null;
    }
}
