import org.json.simple.*;

import javax.imageio.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

public class WeatherAppGUI extends JFrame {
    private JSONObject weatherData;
    public WeatherAppGUI() {
        super("Weather");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setLayout(null);
        setResizable(false);
        addGUIcomponents();
    }
    private void addGUIcomponents() {
        JTextField searchBar = new JTextField();

        searchBar.setBounds(15,15,350, 40);
        searchBar.setFont(new Font("Dialog", Font.PLAIN, 24));
        add(searchBar);

        JLabel weatherConditionImage = new JLabel(loadImage("src/assets/clear.png"));

        weatherConditionImage.setBounds(0, 125, 450, 217);
        add(weatherConditionImage);

        JLabel temperatureText = new JLabel("10 C");

        temperatureText.setBounds(0, 370, 450, 50);
        temperatureText.setFont(new Font("Dialog", Font.BOLD, 42));
        temperatureText.setHorizontalAlignment(SwingConstants.HORIZONTAL);
        add(temperatureText);

        JLabel weatherConditionText = new JLabel("Clear");

        weatherConditionText.setBounds(0, 420, 450, 36);
        weatherConditionText.setFont(new Font("Dialog", Font.PLAIN, 32));
        weatherConditionText.setHorizontalAlignment(SwingConstants.HORIZONTAL);
        add(weatherConditionText);

        JLabel humidityImage = new JLabel(loadImage("src/assets/humidity.png"));

        humidityImage.setBounds(15, 500, 74, 66);
        add(humidityImage);

        JLabel humidityText = new JLabel("<html><b>Humidity</b> 100%</html>");

        humidityText.setBounds(90, 500, 85, 55);
        humidityText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(humidityText);

        JLabel windspeedImage = new JLabel(loadImage("src/assets/windspeed.png"));

        windspeedImage.setBounds(220, 500, 74, 66);
        add(windspeedImage);

        JLabel windspeedText = new JLabel("<html><b>Windspeed</b> 15km/h</html>");

        windspeedText.setBounds(310, 500, 85, 55);
        windspeedText.setFont(new Font("Dialog", Font.PLAIN, 16));
        add(windspeedText);

        JButton searchButton = new JButton(loadImage("src/assets/search.png"));
        searchButton.getBaselineResizeBehavior();

        searchButton.setBounds(375, 15, 40, 40);
        searchButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String userInput = searchBar.getText();
                if (userInput.replaceAll("\\s", "").length() <= 0){
                    return;
                }
                weatherData = WeatherApp.getWeatherData(userInput);
                String weatherCondition = (String) weatherData.get("weather_condition");
                weatherConditionText.setText(weatherCondition);
                switch (weatherCondition){
                    case ("Clear"):
                        weatherConditionImage.setIcon(loadImage("src/assets/clear.png"));
                        break;
                    case ("Cloudy"):
                        weatherConditionImage.setIcon(loadImage("src/assets/cloudy.png"));
                        break;
                    case ("Rain"):
                        weatherConditionImage.setIcon(loadImage("src/assets/rain.png"));
                        break;
                    case ("Snow"):
                        weatherConditionImage.setIcon(loadImage("src/assets/snow.png"));
                        break;
                }

                double temperature = (double) weatherData.get("temperature");
                temperatureText.setText(temperature + " C");

                long humidity = (long) weatherData.get("humidity");
                humidityText.setText("<html><b>Humidity</b> " + humidity + "%</html>");

                double windspeed = (double) weatherData.get("windspeed");
                windspeedText.setText("<html><b>Windspeed</b> " + windspeed + "km/h</html>");
            }
        });
        add(searchButton);
    }

    private ImageIcon loadImage(String path){
        try{
            BufferedImage image = ImageIO.read(new File(path));
            return new ImageIcon(image);
        }
        catch (IOException e){
            return null;
        }
    }
}
