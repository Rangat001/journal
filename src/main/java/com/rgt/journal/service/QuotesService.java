package com.rgt.journal.service;


import com.rgt.journal.apiResponse.QuotesResponse;
import com.rgt.journal.apiResponse.cache.AppCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;


@Service
public class QuotesService {
    @Value("${Quotes.api.key}")
    private String api_key;

//    private static final String api = "https://api.api-ninjas.com/v1/quotes";

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private AppCache appCache;


    public QuotesResponse getQuote() {
        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-Api-Key", api_key);

        // Create an HTTP entity with headers
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Make the API call
        ResponseEntity<QuotesResponse[]> response = restTemplate.exchange(
                appCache.AppCache.get(AppCache.keys.QOUTE_API.toString()),
                HttpMethod.GET,
                entity,
                QuotesResponse[].class
        );

        // Return the first quote from the response array
        QuotesResponse[] quotes = response.getBody();
        return (quotes != null && quotes.length > 0) ? quotes[0] : null;
    }


}
