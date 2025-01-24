import org.json.simple.*;
import org.json.simple.parser.*;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class WeatherApp {
    public static JSONObject getWeatherData(String locationName){
        JSONArray locationData = getLocationData(locationName);
        JSONObject location = (JSONObject) locationData.get(0);

        double latitude = (double) location.get("latitude");
        double longtitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?latitude="+latitude+"&longitude="+longtitude+"&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FLos_Angeles";
        try {
            HttpURLConnection connection = fetchURLforJSON(urlString);
            if (connection.getResponseCode() != 200){
                System.out.println("Error: Couldn't connect to server API");
                return null;
            }
            StringBuilder result = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());

            while(scanner.hasNext()){
                result.append(scanner.nextLine());
            }

            scanner.close();

            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJSONobject = (JSONObject) parser.parse(String.valueOf(result));

            JSONObject hourly = (JSONObject) resultJSONobject.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = getCurrentIndexOfTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            JSONArray humidityData = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) humidityData.get(index);

            JSONArray weatherCodeData = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long) weatherCodeData.get(index));

            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("humidity", humidity);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("windspeed", windSpeed);

            return weatherData;
        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public static JSONArray getLocationData(String locationName){
        locationName = locationName.replaceAll(" ", "+");
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name="+locationName+"&count=10&language=en&format=json";
        try {
            HttpURLConnection connection = fetchURLforJSON(urlString);
            if (connection.getResponseCode() != 200){
                System.out.println("Error: Couldn't connect to server API");
                return null;
            }
            else {
                StringBuilder result = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                while(scanner.hasNext()){
                    result.append(scanner.nextLine());
                }

                scanner.close();

                connection.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultJSONobject = (JSONObject) parser.parse(String.valueOf(result));

                JSONArray locationData = (JSONArray) resultJSONobject.get("results");
                return locationData;
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    private static HttpURLConnection fetchURLforJSON(String urlString){
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            connection.connect();
            return connection;
        }
        catch (IOException e){
            System.out.println("Can't fetch URL");
        }
        return null;
    }

    private static int getCurrentIndexOfTime(JSONArray timeList){
        String currentTime = getCurrentTime();
        for (int i = 0; i < timeList.size(); i++){
            String time = (String) timeList.get(i);
            if (time.equalsIgnoreCase(currentTime)){
                return i;
            }
        }
        return 0;
    }

    public static String getCurrentTime(){
        LocalDateTime currentTimeData = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedTime = currentTimeData.format(formatter);
        return formattedTime;
    }

    private static String convertWeatherCode(long weatherCode){
        String weatherCondition = " ";
        if (weatherCode == 0L){
            weatherCondition = "Clear";
        }
        else if (weatherCode > 0L && weatherCode <= 3L){
            weatherCondition = "Cloudy";
        }
        else if ((weatherCode >= 51L && weatherCode <= 67L) || (weatherCode >= 80L && weatherCode <= 99L)){
            weatherCondition = "Rain";
        }
        else if (weatherCode >= 71L && weatherCode <= 77L){
            weatherCondition = "Snow";
        }

        return weatherCondition;
    }
}