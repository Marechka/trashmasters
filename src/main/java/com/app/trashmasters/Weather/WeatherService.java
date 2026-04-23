package com.app.trashmasters.Weather;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class WeatherService {

    // 🛑 Replace this with your actual free API key from OpenWeatherMap!
    private final String API_KEY = "YOUR_OPENWEATHER_API_KEY";

    // Grabs the current weather for Tempe, AZ in Fahrenheit (units=imperial)
    private final String API_URL = "https://api.openweathermap.org/data/2.5/weather?q=Tempe,us&units=imperial&appid=" + API_KEY;

    public double getCurrentTemperatureF() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            String response = restTemplate.getForObject(API_URL, String.class);

            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(response);

            // Extracts just the temperature number from the giant JSON response
            double currentTemp = root.path("main").path("temp").asDouble();
            System.out.println("🌤️ Current Tempe temperature pulled: " + currentTemp + "°F");

            return currentTemp;

        } catch (Exception e) {
            System.err.println("❌ Weather API failed, defaulting to 75.0°F to prevent AI crash. Error: " + e.getMessage());
            // It is critical to return a fallback number so the XGBoost model doesn't crash!
            return 75.0;
        }
    }
}