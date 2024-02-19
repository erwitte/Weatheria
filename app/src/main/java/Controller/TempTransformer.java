package Controller;

import java.util.List;

/**
 * A utility class for converting temperature values between Celsius and Fahrenheit.
 * It supports transforming a list of temperatures and toggling between Celsius and Fahrenheit outputs.
 */
public class TempTransformer {
    private boolean isFahrenheit;

    /**
     * Constructs a TempTransformer instance with the default output in Celsius.
     */
    public TempTransformer(){
        this.isFahrenheit = false;
    }

    /**
     * Converts a temperature from Kelvin to Fahrenheit.
     * @param kelvin The temperature in Kelvin.
     * @return The temperature in Fahrenheit, rounded to two decimal places.
     */
    public double kelvingToFahrenheit(double kelvin){
        return kelvinToCelsius(kelvin) * 9/5 + 32;
    }

    /**
     * Converts a temperature from Kelvin to Celsius.
     * @param kelvin The temperature in Kelvin.
     * @return The temperature in Celsius, rounded to two decimal places.
     */
    public double kelvinToCelsius(double kelvin){
        double celsius = kelvin -273.15;
        double modifier = Math.pow(10, 2);
        return Math.floor(celsius * modifier) / modifier;
    }

    /**
     * Converts a list of temperatures from Kelvin to the current output format (Celsius or Fahrenheit).
     * Temperatures are converted every fourth element in the list.
     * @param temps A list of temperature values in Kelvin as strings.
     * @return The list of temperatures converted to the current output format, with units appended.
     */
    public List<String> calculateTemp(List<String> temps){
        if (!isFahrenheit) {
            for (int i = 0; i < temps.size(); i = i + 4) {
                temps.set(i, kelvinToCelsius(Double.parseDouble(temps.get(i))) + " °C");
            }

        } else {
            for (int i = 0; i < temps.size(); i = i + 4) {
                temps.set(i, kelvingToFahrenheit(Double.parseDouble(temps.get(i))) + " °F");
            }
        }
        return temps;
    }

    /**
     * Toggles the output temperature format between Celsius and Fahrenheit.
     */
    public void changeIsFahrenheit(){
        if (isFahrenheit)
            isFahrenheit = false;
        else isFahrenheit = true;
    }

    /**
     * Gets the current temperature format output setting.
     * @return true if the output format is Fahrenheit, false if it is Celsius.
     */
    public boolean getIsFahrenheit(){
        return isFahrenheit;
    }
}
