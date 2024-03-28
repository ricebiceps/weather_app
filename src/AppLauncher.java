import javax.swing.*;

public class AppLauncher {
    public static void main(String[] args) {
        
        SwingUtilities.invokeLater(new Runnable(){
            @Override
            public void run() {
                // display the weather gui
                new WeatherAppGUI().setVisible(true);
            }
        });
    }
}
