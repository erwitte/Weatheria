package Controller;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class FileWriterReader {
    private String currentWeather;
    private final Context context;
    private String fiveDaysForecast;
    private String exactName;

    public FileWriterReader(Context context){
        this.context = context;
    }

    public void setCurrentWeather(String currentWeather){
        this.currentWeather = currentWeather;
        if (currentWeather != null && fiveDaysForecast != null
                && exactName != null)
            writeToFile();
    }

    public void setFiveDaysForecast(String fiveDaysForecast) {
        this.fiveDaysForecast = fiveDaysForecast;
        if (currentWeather != null && fiveDaysForecast != null
                && exactName != null)
            writeToFile();
    }

    public void setExactName(String exactName){
        this.exactName = exactName;
        if (currentWeather != null && fiveDaysForecast != null
                && exactName != null)
            writeToFile();
    }

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

    public String readCurrentWeather(){
        return readLineAt(1);
    }

    public String readFiveDaysForecast(){
        return readLineAt(2);
    }

    public String readExactName(){
        return readLineAt(3);
    }
}