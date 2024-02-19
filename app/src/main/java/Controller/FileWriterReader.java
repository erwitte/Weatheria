package Controller;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

/**
 * Handles reading and writing weather data and location names to internal storage.
 */
public class FileWriterReader {
    private String currentWeather;
    private final Context context;
    private String fiveDaysForecast;
    private String exactName;

    /**
     * Initializes a new instance of FileWriterReader with the application's context.
     *
     * @param context The application's context.
     */
    public FileWriterReader(Context context){
        this.context = context;
    }

    /**
     * Sets and writes the current weather data to file if all necessary data is available.
     *
     * @param currentWeather The current weather data in string format.
     */
    public void setCurrentWeather(String currentWeather){
        this.currentWeather = currentWeather;
        if (currentWeather != null && fiveDaysForecast != null
                && exactName != null)
            writeToFile();
    }

    /**
     * Sets and writes the five-day weather forecast data to file if all necessary data is available.
     *
     * @param fiveDaysForecast The five-day weather forecast data in string format.
     */
    public void setFiveDaysForecast(String fiveDaysForecast) {
        this.fiveDaysForecast = fiveDaysForecast;
        if (currentWeather != null && fiveDaysForecast != null
                && exactName != null)
            writeToFile();
    }

    /**
     * Sets and writes the exact name of the location to file if all necessary data is available.
     *
     * @param exactName The exact name of the location.
     */
    public void setExactName(String exactName){
        this.exactName = exactName;
        if (currentWeather != null && fiveDaysForecast != null
                && exactName != null)
            writeToFile();
    }

    /**
     * Writes the current weather, five-day forecast, and location name to internal storage.
     */
    private void writeToFile(){
        try {
            FileOutputStream fos = context.openFileOutput("weatherData.txt", Context.MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(fos);
            writer.write(this.currentWeather + "\n");
            writer.write(this.fiveDaysForecast + "\n");
            writer.write(this.exactName + "\n");
            writer.close();
        } catch (IOException e){
            Log.e("IOException", "FileWriter", e);
        }
    }

    /**
     * Reads a specific line from the saved file, identified by line number.
     *
     * @param lineNumber The line number to read from the file.
     * @return The content of the specified line, or null if an error occurs.
     */
    public String readLineAt(int lineNumber){
        try {
            FileInputStream fis = context.openFileInput("weatherData.txt");
            BufferedReader reader = new BufferedReader(new InputStreamReader(fis));
            String line;
            int currentLine = 0;
            while ((line = reader.readLine()) != null){
                currentLine++;
                if (currentLine == lineNumber) {
                    reader.close();
                    return line;
                }
            }
            reader.close();
        } catch (IOException e){
            Log.e("IOException", "FileReader", e);
        }
        return null;
    }

    /**
     * Reads the current weather data from internal storage.
     *
     * @return The current weather data as a string, or null if an error occurs.
     */
    public String readCurrentWeather(){
        return readLineAt(1);
    }

    /**
     * Reads the five-day weather forecast data from internal storage.
     *
     * @return The five-day forecast data as a string, or null if an error occurs.
     */
    public String readFiveDaysForecast(){
        return readLineAt(2);
    }

    /**
     * Reads the exact name of the location from internal storage.
     *
     * @return The exact name of the location as a string, or null if an error occurs.
     */
    public String readExactName(){
        return readLineAt(3);
    }
}