package com.example.customer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;

import feign.hystrix.FallbackFactory;
import feign.hystrix.HystrixFeign;

@RestController
public class CustomerController {

    @Value("${preferences.api.url}")
    private String remoteURL;

    private static final String RESPONSE_STRING_FORMAT = "C100 * %s *";

    @RequestMapping("/")
    public ResponseEntity<String> getCustomer() {
        try {

            FallbackFactory<PreferencesService> fallbackFactory = cause -> () -> {
                //cause.printStackTrace();
                return "{\"P0\":\"Voilet\", \"P2\":\"Micro\",\"Action\":\"Fallback\"}";
            };
            PreferencesService client = HystrixFeign.builder().target(PreferencesService.class, remoteURL,
                    fallbackFactory);
            String response = client.getPreferences();
            return ResponseEntity.ok(String.format(RESPONSE_STRING_FORMAT, response));
        } catch (RestClientException ex) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(String.format(RESPONSE_STRING_FORMAT, ex.getCause()));
        }
    }

}
