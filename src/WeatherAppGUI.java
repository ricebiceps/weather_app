import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.awt.*;
import java.awt.event.ActionListener;
import org.json.simple.JSONObject;
import java.awt.event.ActionEvent;


public class WeatherAppGUI extends JFrame{
    private JSONObject weatherData;

    public WeatherAppGUI() {

        //set up gui and add title
        super("Weather App");

        //configure gui to end the program when the window is closed
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        //set the size of the window
        setSize(450,650);

        //center the window
        setLocationRelativeTo(null);

        //make layout null to manually position components
        setLayout(null);

        //prevent resizing
        setResizable(false);

        addGuiComponents();
    }

    private void addGuiComponents(){

        //search field
        JTextField searchTextField = new JTextField();

        //set the location and size of our component
        searchTextField.setBounds(15,15,351,45);

        //change the font style and size
        searchTextField.setFont(searchTextField.getFont().deriveFont(20f));

        //add the component to the window
        add(searchTextField);

        //weather image
        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/cloudy.png"));
        weatherConditionImage.setBounds(0,125,450,217);
        add(weatherConditionImage);

        //temperature text
        JLabel temperatureText = new JLabel("10 C");
        temperatureText.setBounds(0, 350, 450, 54);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 48));

        //center the text
        temperatureText.setHorizontalAlignment(SwingConstants.CENTER);
        add(temperatureText);   

        //weather condition description
        JLabel weatherConditionDesc = new JLabel("Cloudy");
        weatherConditionDesc.setBounds(0,405,450,36);
        weatherConditionDesc.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionDesc.setHorizontalAlignment(SwingConstants.CENTER);
        add(weatherConditionDesc);

        //humidity image
        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));
        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        //humidity text
        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");
        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        //windspeed image
        JLabel windSpeedImage = new JLabel(loadImage("src/assets/windspeed.png"));
        windSpeedImage.setBounds(220, 500, 74, 66);
        add(windSpeedImage);

        //windspeed text
        JLabel windSpeedText = new JLabel("<html><b>Windspeed</b> 10km/h</html>");
        windSpeedText.setBounds(295, 500, 85, 55);
        windSpeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windSpeedText);

        //search button
        JButton searchButton = new JButton(loadImage("src/assets/search.png"));

        //set the location of the cursor, and change to hand when hovered over
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.setBounds(375,13,47,45);
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //get location from user
                String userInput = searchTextField.getText();
                
                //validate input
                if(userInput.replaceAll("\\s","").length()<= 0){
                    return;
                }
            
            //retrieve weather data
            weatherData = WeatherApp.getWeatherData(userInput);
            
            //update gui

            //get weather condition
            String weatherCondition = (String) weatherData.get("weather_condition");
            
            //update weather image based on condition
            switch(weatherCondition){
                case "Clear":
                    weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                    break;
                case "Cloudy":
                    weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                    break;
                case "Rain":
                    weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                    break;
                case "Snow":
                    weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                    break;
            }
            //update temperature text
            double temperature = (double) weatherData.get("temperature");
            temperatureText.setText(temperature + " C");

            //update weather condition text
            weatherConditionDesc.setText(weatherCondition);

            //update humidity text
            long humidity = (long) weatherData.get("humidity");
            humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

            //update windspeed text
            double windspeed = (double) weatherData.get("windSpeed");
            windSpeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        
        });
        add(searchButton);

        
    }

    //load image from resource path
    private ImageIcon loadImage(String resourcePath){
        try{
            //read image from file
            BufferedImage image = ImageIO.read(new File(resourcePath));

            //return image as an icon
            return new ImageIcon(image);

        }catch(IOException e){
            e.printStackTrace();
        }
        
        System.out.println("Could not be resource");
        return null;
    }
}
