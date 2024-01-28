package com.example;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AirportLocator {
    public static double[] getLocation(String icao) {
        try {
            // Using the URL factory method
            String key = "X9HyZudJj0e7Y0inZWCQWA==cPiXQePFOwRgdJBw";
            String address = "https://api.api-ninjas.com/v1/airports?icao=";
            URL url = new URL(address + icao);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");
            connection.setRequestProperty("x-api-key", key);

            try (InputStream responseStream = connection.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseStream);
                double[] ret = new double[2];
                if (root.isArray() && root.size() > 0) {
                    JsonNode airportInfo = root.get(0); // Assuming there's only one airport in the response
                    
                    // Extract latitude and longitude
                    ret[0] = airportInfo.get("latitude").asDouble();
                    ret[1] = airportInfo.get("longitude").asDouble();
                }
                return ret;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception or return an error message
            return null;
        }
    }
}
