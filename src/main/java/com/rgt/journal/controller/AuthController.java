package com.rgt.journal.controller;

import com.rgt.journal.Repository.UserRepository;
import com.rgt.journal.entity.UserEntity;
import com.rgt.journal.service.UserDetailsServiceImpl;
import com.rgt.journal.utils.JWTutil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authorization.method.AuthorizeReturnObject;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Value("${spring.security.oauth.client.registration.google.clientId}")
    String client_id;

    @Value("${spring.security.oauth.client.registration.google.clientSecrete}")
    String clientsecret;

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

    @GetMapping("/googleCallback")
    public ResponseEntity<?> handelGoogleCallback(@RequestParam String code){
        try {
            //1. Exchange auth code for tokens
            String tokenEndpoint = "https://oauth2.googleapis.com/token";

            MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
            params.add("code",code);
            params.add("client_id",client_id);
            params.add("client_secret",clientsecret);
            params.add("redirect_uri","https://developers.google.com/oauthplayground");
            params.add("grant_type","authorization_code");

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String,String>> request = new HttpEntity<>(params,headers);

            ResponseEntity<Map> token_response = restTemplate.postForEntity(tokenEndpoint,request,Map.class);

            String tokenid = (String) token_response.getBody().get("id_token");
            String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + tokenid;

            ResponseEntity<Map> userInfoResponse = restTemplate.getForEntity(userInfoUrl,Map.class);
            if (userInfoResponse.getStatusCode() == HttpStatus.OK){
                Map<String,Object> userInfo = userInfoResponse.getBody();
                String email = (String) userInfo.get("email");
                UserDetails userDetails = null;
                try{
                    userDetails = userDetailsService.loadUserByUsername(email);
                }catch (Exception e){
                    UserEntity user = new UserEntity();

                    user.setEmail(email);
                    user.setUsername(email);
                    user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                    user.setRoles(Arrays.asList("USER"));
                    userRepository.save(user);
                }
                String jwtToken = jwTutil.generateToken(email);
                return ResponseEntity.ok(Collections.singletonMap("token",jwtToken));
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            log.error("error occured when hadleGoogleCallback",e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}
