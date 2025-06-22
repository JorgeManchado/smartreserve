package com.gestion.reservas.security;


import org.springframework.stereotype.Component;

@Component
public class GoogleTokenStore {

    private String accessToken;

    public void storeAccessToken(String token) {
        this.accessToken = token;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public boolean hasAccessToken() {
        return accessToken != null;
    }
}
