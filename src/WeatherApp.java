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
        
        //get the location data
        JSONArray locationData = getLocationData(locationName);

        //get the latitude and longitude
        JSONObject location = (JSONObject) locationData.get(0);
        double latitude = (double) location.get("latitude");
        double longitude = (double) location.get("longitude");

        //build api url
        String urlString = "https://api.open-meteo.com/v1/forecast?" +
        "latitude=" + latitude + "&longitude=" + longitude +  
        "&hourly=temperature_2m,relative_humidity_2m,weather_code,wind_speed_10m&timezone=America%2FNew_York";
        try{

            //fetch api response
            HttpURLConnection connection = fetchApiResponse(urlString);
            
            //check if connection was successful
            if(connection.getResponseCode() != 200){
                System.out.println("Error: Could not connect to API");
                return null;
            }
            
            //store resulting json
            StringBuilder resultJson = new StringBuilder();
            Scanner scanner = new Scanner(connection.getInputStream());
            while(scanner.hasNext()){
                //read and store to json
                resultJson.append(scanner.nextLine());
            }

            //close scanner
            scanner.close();

            //close connection
            connection.disconnect();

            //parse json
            JSONParser parser = new JSONParser();
            JSONObject resultJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

            //get hourly data
            JSONObject hourly = (JSONObject) resultJsonObj.get("hourly");

            //get time data
            JSONArray time = (JSONArray) hourly.get("time");
            int index = findIndexOfCurrentTime(time);

            //get temperature data
            JSONArray temperatureData = (JSONArray) hourly.get("temperature_2m");
            double temperature = (double) temperatureData.get(index);

            //get humidity data
            JSONArray humidityData = (JSONArray) hourly.get("relative_humidity_2m");
            long humidity = (long) humidityData.get(index);

            //get weather code data
            JSONArray weathercode = (JSONArray) hourly.get("weather_code");
            String weatherCondition = convertWeatherCode((long)weathercode.get(index));

            //get wind speed data
            JSONArray windSpeedData = (JSONArray) hourly.get("wind_speed_10m");
            double windSpeed = (double) windSpeedData.get(index);

            //initialize json object and store weather data
            JSONObject weatherData = new JSONObject();
            weatherData.put("temperature", temperature);
            weatherData.put("humidity", humidity);
            weatherData.put("weather_condition", weatherCondition);
            weatherData.put("windSpeed", windSpeed);

            //return the json object
            return weatherData;
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }

    //retrieves the geolocation data
    public static JSONArray getLocationData(String locationName){
        //replace spaces with + for url
        locationName = locationName.replaceAll(" ","+");

        //build api url
        String urlString = "https://geocoding-api.open-meteo.com/v1/search?name=" +
            locationName + "&count=1&language=en&format=json";
        
        try{
            //fetch api response
            HttpURLConnection connection = fetchApiResponse(urlString);
            
            //check if connection was successful
            if(connection.getResponseCode() != 200){
                System.out.println("Error: " + connection.getResponseCode());
                return null;
            }
            else{

                //store resulting json
                StringBuilder resultJson = new StringBuilder();
                Scanner scanner = new Scanner(connection.getInputStream());

                while(scanner.hasNext()){
                    //read and store to json
                    resultJson.append(scanner.nextLine());
                }

                //close scanner
                scanner.close();

                //close connection
                connection.disconnect();

                //parse json
                JSONParser parser = new JSONParser();
                JSONObject resultsJsonObj = (JSONObject) parser.parse(String.valueOf(resultJson));

                //get the list of location data the API generated from location naem
                JSONArray locationData = (JSONArray) resultsJsonObj.get("results");
                return locationData;
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    //method to fetch api response
    private static HttpURLConnection fetchApiResponse(String urlString) throws Exception{
        try{
            //create url object
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            //set request method to get 
            connection.setRequestMethod("GET");

            //connect to the api
            connection.connect();
            return connection;
        }catch(IOException e){
            e.printStackTrace();
        }
        return null;

    }
    //method to find the index of the current time
    private static int findIndexOfCurrentTime(JSONArray timeList){

        //get current time
        String currentTime = getCurrentTime();

        //iterate through the time list
        for(int i=0; i <timeList.size();i++){
            String time = (String) timeList.get(i);
            if(time.equals(currentTime)){
                return i;
            }
        }

        return 0;
    }

    //method to get the current time
    public static String getCurrentTime(){

        //get the current date and time
        LocalDateTime currentDateTime = LocalDateTime.now();

        //set up a formatter
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH':00'");

        //format the date and time
        String formattedDateTime = currentDateTime.format(formatter);

        return formattedDateTime;
    }

    //method to convert weather code to weather condition string
    private static String convertWeatherCode(long weathercode){
        //initialize weather condition string
        String weatherCondition = "";

        //set weather condition based on weather code
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
