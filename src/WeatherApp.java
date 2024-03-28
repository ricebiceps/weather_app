import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.net.URL;
import java.time.LocalDateTime;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.util.Scanner;
import java.time.format.DateTimeFormatter;

import org.json.simple.JSONArray;

public class WeatherApp {
    public static JSONObject getWeatherData(String locationName) {
        
        JSONArray locationData = getLocationData(locationName);

        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        String urlString = "https://api.open-meteo.com/v1/forecast?" +
        "latitude=" + latitude + "&longitude=" + longitude +  
        "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FNew_York";

        try{

            HttpURLConnection connection = fetchApiResponse(urlString);
            if(connection.getResponseCode() != 200){
                System.out.println("Eroor: Could not connect to API");
                return null;
            }

            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNext()){
                resultJson.append(scanner.nextLine());
            }

            scanner.close();

            connection.disconnect();

            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);
            
            JSONArray humidityData = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) humidityData.get(index);

            JSONArray weathercode = (JSONArray) hourly.get("weathercode");
            String weatherCondition = convertWeatherCode((long)weathercode.get(index));

            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index);

            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("humidity", humidity);
            weatherData.put("weatherCondition", weatherCondition);
            weatherData.put("windSpeed", windSpeed);

            return weatherData;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public static JSONArray getLocationData(String locationName){
        locationName = locationName.replaceAll(" ","+");

        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
            locationName + "&count=1&language=en&format=json";
        
        try{
            HttpURLConnection connection = fetchApiResponse(urlString);
            
            if(connection.getResponseCode() != 200){
                System.out.println("Error: " + connection.getResponseCode());
                return null;
            }
            else{
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                while(scanner.hasNext()){
                    resultJson.append(scanner.nextLine());
                }

                scanner.close();

                connection.disconnect();

                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static HttpURLConnection fetchApiResponse(String urlString) throws Exception{
        try{
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            connection.connect();
            return connection;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;

    }

    private static int findIndexOfCurrentTime(JSONArray timeList){
        String currentTime = getCurrentTime();

        for(int i=0; i <timeList.size();i++){
            String time = (String) timeList.get(i);
            if(time.equals(currentTime)){
                return i;
            }
        }

        return 0;
    }

    public static String getCurrentTime(){
        LocalDateTime currentDateTime = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    private static String convertWeatherCode(long weathercode){
        String weatherCondition = "";
        if(weathercode == 0L){
            
            weatherCondition = "Clear";
        }else if(weathercode <= 3L && weathercode > 0L){
            
            weatherCondition = "Cloudy";
        }else if(weathercode >= 51L && weathercode <= 67L
        || weathercode >= 80L && weathercode <= 99L){
            
            weatherCondition = "Rain";
        }else if(weathercode >= 71L && weathercode <= 77L){
            
            weatherCondition = "Snow";
        }
        return weatherCondition;
    }

}