package com.rgt.journal.controller;

import com.rgt.journal.Repository.UserRepository;
import com.rgt.journal.entity.UserEntity;
import com.rgt.journal.service.UserDetailsServiceImpl;
import com.rgt.journal.utils.JWTutil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Value("${spring.security.oauth.client.registration.google.clientId}")
    String google_client_id;

    @Value("${spring.security.oauth.client.registration.google.clientSecrete}")
    String google_clientsecret;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserDetailsServiceImpl userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTutil jwTutil;

//    @GetMapping("/googleCallback")
//    public ResponseEntity<?> handelGoogleCallback(@RequestParam String code){
//        try {
//            //1. Exchange auth code for tokens
//            String tokenEndpoint = "https://oauth2.googleapis.com/token";
//
//            MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
//            params.add("code",code);
//            params.add("client_id",client_id);
//            params.add("client_secret",clientsecret);
//            params.add("redirect_uri","https://developers.google.com/oauthplayground");
//            params.add("grant_type","authorization_code");
//
//            HttpHeaders headers = new HttpHeaders();
//            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//
//            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params,headers);
//
//            ResponseEntity<Map> token_response = restTemplate.postForEntity(tokenEndpoint,request,Map.class);
//
//            String tokenid = (String) token_response.getBody().get("id_token");
//            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + tokenid;
//
//            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl,Map.class);
//            if (userInfoResponse.getStatusCode() == HttpStatus.OK){
//                Map<String,Object> userInfo = userInfoResponse.getBody();
//                String email = (String) userInfo.get("email");
//                UserDetails userDetails = null;
//                try{
//                    userDetails = userDetailsService.loadUserByUsername(email);
//                }catch (Exception e){
//                    UserEntity user = new UserEntity();
//
//                    user.setEmail(email);
//                    user.setUsername(email);
//                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
//                    user.setRoles(Arrays.asList("USER"));
//                    userRepository.save(user);
//                }
//                String jwtToken = jwTutil.generateToken(email);
//                return ResponseEntity.ok(Collections.singletonMap("token",jwtToken));
//            }
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//        } catch (Exception e) {
//            log.error("error occured when hadleGoogleCallback",e);
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
//        }
//    }



    @GetMapping("/google/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam Map<String, String> requestBody){
        try {
            String code = requestBody.get("code");

            //1. Exchange auth code for tokens
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
            params.add("code", code);
            params.add("client_id", google_client_id);
            params.add("client_secret", google_clientsecret);
            // IMPORTANT: Use your actual redirect URI that matches OAuth app configuration
            params.add("redirect_uri", "http://localhost:8080/auth/google/callback"); // Update this to match your domain
            params.add("grant_type", "authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> token_response = restTemplate.postForEntity(tokenEndpoint, request, Map.class);

            String idToken = (String) token_response.getBody().get("id_token");

            // Use the correct tokeninfo endpoint
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;

            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl, Map.class);

            if (userInfoResponse.getStatusCode() == HttpStatus.OK){
                Map<String,Object> userInfo = userInfoResponse.getBody();

                // Verify the audience (client_id) for security
                String aud = (String) userInfo.get("aud");
                if (!google_client_id.equals(aud)) {
                    log.error("Invalid audience in ID token: {}", aud);
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
                }

                String email = (String) userInfo.get("email");
                String name = (String) userInfo.get("name");

                // Check if user exists, create if not
                UserDetails userDetails = null;
                try{
                    userDetails = userDetailsService.loadUserByUsername(name);
                } catch (UsernameNotFoundException e) {
                    // Create new user
                    UserEntity user = new UserEntity();
                    user.setEmail(email);
                    user.setUsername(name);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRoles(Arrays.asList("USER"));


                    userRepository.save(user);
                    log.info("Created new user from Google OAuth: {}",name);
                }

                String jwtToken = jwTutil.generateToken(name);
                String htmlResponse = "<!DOCTYPE html>" +
                        "<html><head><title>Redirecting...</title></head>" +
                        "<body><script>" +
                        "localStorage.setItem('jwt_token', '" + jwtToken + "');" +
                        "window.location.href = '/RGT/sonet/index.html';" +
                        "</script></body></html>";

                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(htmlResponse);
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception e) {
            log.error("Error occurred when handling Google OAuth callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "OAuth authentication failed"));
        }
    }

    //                                    Github Auth


    @Value("${spring.security.oauth.client.registration.github.clientId}")
    String githubClientId;

    @Value("${spring.security.oauth.client.registration.github.clientSecrete}")
    String githubClientSecret;

    @GetMapping("/github/callback")
    public ResponseEntity<?> handleGitHubCallback(@RequestParam Map<String, String> requestBody){
        try {
            String code = requestBody.get("code");

            //1. Exchange auth code for access token
            String tokenEndpoint = "https://github.com/login/oauth/access_token";

            MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
            params.add("client_id", githubClientId); // Add this to your application properties
            params.add("client_secret", githubClientSecret); // Add this to your application properties
            params.add("code", code);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            headers.set("Accept", "application/json");

            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params, headers);

            ResponseEntity<Map> tokenResponse = restTemplate.postForEntity(tokenEndpoint, request, Map.class);

            String accessToken = (String) tokenResponse.getBody().get("access_token");

            if (accessToken == null) {
                log.error("Failed to get access token from GitHub");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            //2. Get user information
            String userInfoUrl = "https://api.github.com/user";

            HttpHeaders userHeaders = new HttpHeaders();
            userHeaders.set("Authorization", "Bearer " + accessToken);
            userHeaders.set("Accept", "application/vnd.github.v3+json");

            HttpEntity<?> userRequest = new HttpEntity<>(userHeaders);

            ResponseEntity<Map> userInfoResponse = restTemplate.exchange(
                    userInfoUrl, HttpMethod.GET, userRequest, Map.class);

            if (userInfoResponse.getStatusCode() == HttpStatus.OK) {
                Map<String, Object> userInfo = userInfoResponse.getBody();

                // Get user email (GitHub might not provide email in user endpoint)
                String email = (String) userInfo.get("email");
                String username = (String) userInfo.get("login");


                // If email is null, try to get it from emails endpoint
                if (email == null) {
                    email = getGitHubUserEmail(accessToken);
                }

                // Use username as fallback if email is still null
                String userIdentifier = email != null ? email : username;

                // Check if user exists, create if not
                UserDetails userDetails = null;
                try{
                    userDetails = userDetailsService.loadUserByUsername(username);
                } catch (UsernameNotFoundException e) {
                    // Create new user
                    UserEntity user = new UserEntity();
                    user.setEmail(email);
                    user.setUsername(username);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRoles(Arrays.asList("USER"));

                    userRepository.save(user);
                    log.info("Created new user from GitHub OAuth: {}", username);
                }

                String jwtToken = jwTutil.generateToken(username);

                String htmlResponse = "<!DOCTYPE html>" +
                        "<html><head><title>Redirecting...</title></head>" +
                        "<body><script>" +
                        "localStorage.setItem('jwt_token', '" + jwtToken + "');" +
                        "window.location.href = '/RGT/sonet/index.html';" +
                        "</script></body></html>";

                return ResponseEntity.ok()
                        .contentType(MediaType.TEXT_HTML)
                        .body(htmlResponse);

            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        } catch (Exception e) {
            log.error("Error occurred when handling GitHub OAuth callback", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "GitHub OAuth authentication failed"));
        }
    }

    private String getGitHubUserEmail(String accessToken) {
        try {
            String emailsUrl = "https://api.github.com/user/emails";

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + accessToken);
            headers.set("Accept", "application/vnd.github.v3+json");

            HttpEntity<?> request = new HttpEntity<>(headers);

            ResponseEntity<List> emailsResponse = restTemplate.exchange(
                    emailsUrl, HttpMethod.GET, request, List.class);

            if (emailsResponse.getStatusCode() == HttpStatus.OK) {
                List<Map<String, Object>> emails = emailsResponse.getBody();

                // Find primary email
                for (Map<String, Object> emailObj : emails) {
                    Boolean primary = (Boolean) emailObj.get("primary");
                    Boolean verified = (Boolean) emailObj.get("verified");

                    if (primary != null && primary && verified != null && verified) {
                        return (String) emailObj.get("email");
                    }
                }

                // If no primary email, return first verified email
                for (Map<String, Object> emailObj : emails) {
                    Boolean verified = (Boolean) emailObj.get("verified");
                    if (verified != null && verified) {
                        return (String) emailObj.get("email");
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Failed to get GitHub user email", e);
        }

        return null;
    }

    @GetMapping("/data")
    public Map<String, String> getConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("GOOGLE_CLIENT_ID", google_client_id);
        config.put("GITHUB_CLIENT_ID", githubClientId);
        return config;
    }


}
