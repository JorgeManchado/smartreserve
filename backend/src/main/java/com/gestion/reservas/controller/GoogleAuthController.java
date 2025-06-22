package com.gestion.reservas.controller;

import com.gestion.reservas.security.GoogleTokenStore;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/google")
@RequiredArgsConstructor
public class GoogleAuthController {

    @Value("${google.client.id}")
    private String clientId;

    @Value("${google.client.secret}")
    private String clientSecret;

    @Value("${google.redirect.uri}")
    private String redirectUri;

    private final RestTemplate restTemplate = new RestTemplate();
    private final GoogleTokenStore tokenStore;


    @GetMapping("/auth")
    public void redirectToGoogle(HttpServletResponse response) throws IOException {
        String authUrl = UriComponentsBuilder
                .fromUriString("https://accounts.google.com/o/oauth2/v2/auth")
                .queryParam("client_id", clientId)
                .queryParam("redirect_uri", redirectUri)
                .queryParam("response_type", "code")
                .queryParam("scope", "https://www.googleapis.com/auth/calendar.events")
                .queryParam("access_type", "offline")
                .build()
                .toUriString();

        response.sendRedirect(authUrl);
    }

    @GetMapping("/oauth2callback")
    public ResponseEntity<String> handleCallback(@RequestParam("code") String code) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
                "https://oauth2.googleapis.com/token", request, Map.class
        );

        if (response.getStatusCode().is2xxSuccessful()) {
            String accessToken = (String) response.getBody().get("access_token");
            tokenStore.storeAccessToken(accessToken);

            String html = """
    <html>
    <head>
      <script>
        window.opener.postMessage('google-auth-success', '*');
        window.close();
      </script>
    </head>
    <body>
      Autenticación correcta. Puedes cerrar esta ventana.
    </body>
    </html>
""";


            HttpHeaders htmlHeaders = new HttpHeaders();
            htmlHeaders.setContentType(MediaType.TEXT_HTML);

            return new ResponseEntity<>(html, htmlHeaders, HttpStatus.OK);
           // return ResponseEntity.ok("Autenticación correcta. Puedes cerrar esta ventana.");
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error al autenticar con Google");
    }

    @GetMapping("/token-status")
    public ResponseEntity<Boolean> tokenPresente() {
        return ResponseEntity.ok(tokenStore.hasAccessToken());
    }

}
