package com.example;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CoordinateWeather {
    private static final String API_KEY = "68afcb4256b81f3aad86670b99692526";

    public static ArrayList<String> getWeather(String icao, double[] coordinates) {
        if (coordinates == null) {
            return null;
        }
        try {
            String address = "https://api.openweathermap.org/data/2.5/weather?lat=" + coordinates[0] + "&lon=" + coordinates[1] + "&appid=";
            URL url = new URL(address + API_KEY);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("accept", "application/json");

            try (InputStream responseStream = connection.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(responseStream);
                ArrayList<String> ret = new ArrayList<>();
                ret.add(icao.toUpperCase());
                ret.add(getUTC());

                if (root.has("wind")) {
                    JsonNode wind = root.get("wind");
                    ret.add(wind.get("deg").asText() + Integer.toString(wind.get("speed").asInt() * 3));
                }

                /*
                 * Needs to be fixed!
                 * Wrong output for METAR format
                 */
                if (root.has("weather") && root.get("weather").isArray() && root.get("weather").size() > 0) {
                    JsonNode weather = root.get("weather").get(0); // Assuming there's only one weather entry in the array
                    ret.add(weather.get("description").asText());
                }

                if (root.has("visibility")) {
                    ret.add(Integer.toString(root.get("visibility").asInt() / 1000) + "SM");
                }

                if (root.has("clouds")) {
                    JsonNode clouds = root.get("clouds");
                    if (clouds.has("all")) {
                        int cloudCoverage = clouds.get("all").asInt();
                        if (cloudCoverage <= 30) {
                            ret.add("CLR" + cloudCoverage);
                        } else if (cloudCoverage <= 70) {
                            ret.add("SCT" + cloudCoverage);
                        } else {
                            ret.add("BKN" + cloudCoverage);
                        }
                    }
                }

                if (root.has("main")) {
                    JsonNode main = root.get("main");
                    ret.add(Integer.toString(main.get("temp").asInt() - 272) + "/" +
                            Integer.toString((main.get("temp").asInt() - 272) - ((100 - main.get("humidity").asInt()) / 5)));
                    ret.add("A" + Integer.toString((int) (main.get("pressure").asInt() * 2.956)));
                }

                return ret;
            }
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception or return an error message
            return null;
        }
    }

    private static String getUTC() {
        String time = Instant.now().toString();
        time = time.substring(8, 10) + time.substring(11, 13) + time.substring(14, 16);
        return time;
    }
}