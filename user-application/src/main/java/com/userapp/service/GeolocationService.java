package com.userapp.service;


import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;
import java.util.Map;

@Service
public class GeolocationService {

    private final List<String> BANNED_COUNTRIES = List.of("Syria", "Afghanistan", "Iran", "North Korea");
    private final RestTemplate restTemplate = new RestTemplate();

    public String getCountryFromIp(String ipAddress) {
        // Fallback for local testing environments
        if (ipAddress.equals("0:0:0:0:0:0:0:1") || ipAddress.equals("127.0.0.1")) {
            return "United States";
        }
        try {
            String url = "https://ipapi.co" + ipAddress + "/json/";
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            return response != null ? (String) response.get("country_name") : "Unknown";
        } catch (Exception e) {
            return "Unknown";
        }
    }

    public boolean isCountryBanned(String country) {
        return BANNED_COUNTRIES.stream().anyMatch(c -> c.equalsIgnoreCase(country));
    }


}
