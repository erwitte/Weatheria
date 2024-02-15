package View;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.util.Log;

import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;

import com.example.weatheria.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import Controller.FileWriterReader;
import Controller.InternetChecker;
import Controller.WeatherFetcher;

public class VideoPlayer {
    private ExoPlayer exoPlayer;
    private String videoUrl;
    private Context context;
    private PlayerView playerView;
    private WeatherFetcher weatherFetcher;
    private final FileWriterReader fileWriterReader;

    public VideoPlayer(Context context, PlayerView playerView){
        this.fileWriterReader = new FileWriterReader(context);
        this.context = context;
        this.exoPlayer = new ExoPlayer.Builder(context).build();
        this.playerView = playerView;
        playerView.setPlayer(exoPlayer);
        this.weatherFetcher = new WeatherFetcher(context, fileWriterReader);
    }

    public void play(){
        InternetChecker internetChecker = new InternetChecker(context);
        if (internetChecker.hasInternet()){
            videoUrl = decideVideo();
            DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)));
            exoPlayer.setMediaSource(mediaSource);

        } else {
            videoUrl = "android.resource://" + context.getPackageName() + "/" + R.raw.no_internet;
            DataSource.Factory dataSourceFactory = new DefaultDataSource.Factory(context);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(videoUrl)));
            exoPlayer.setMediaSource(mediaSource);
        }
        exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
        exoPlayer.prepare();
        exoPlayer.play();
    }

    private String decideVideo(){
        JSONObject currentWeather = loadData();
        try{
            JSONArray weatherArray = currentWeather.getJSONArray("weather");
            JSONObject currentWeatherObject = weatherArray.getJSONObject(0);
            int id = currentWeatherObject.getInt("id");
            if (id > 199 && id < 300){
                return "android.resource://" + context.getPackageName() + "/" + R.raw.thunderstorm;
            } else if (id > 299 && id < 400)
                return "android.resource://" + context.getPackageName() + "/" + R.raw.squall;
            else if (id > 499 && id < 600)
                return "android.resource://" + context.getPackageName() + "/" + R.raw.rain;
            else if (id > 599 && id < 700)
                return "android.resource://" + context.getPackageName() + "/" + R.raw.snow;
            else if (id > 699 && id < 800)
                return "android.resource://" + context.getPackageName() + "/" + R.raw.mist;
            else if (id == 800)
                return "android.resource://" + context.getPackageName() + "/" + R.raw.clear;
            else if (id > 800 && id < 900)
                return "android.resource://" + context.getPackageName() + "/" + R.raw.clouds;
        } catch(JSONException e){
            Log.e("JSONError", "player, decideVideo", e);
        }
        return null;
    }

    private JSONObject loadData(){
        File lastSearch = new File(context.getFilesDir(), "weatherData.txt");
        if (lastSearch.exists()) {
            try {
                // lädt zuletzt geladenen ort aus dem speicher und fragt dessen aktuelles wetter ab
                JSONObject weatherData = new JSONObject(fileWriterReader.readCurrentWeather());
                JSONObject coordsObjet = weatherData.getJSONObject("coord");
                double latittude = coordsObjet.getDouble("lat");
                double longitude = coordsObjet.getDouble("lon");
                return weatherFetcher.getCurrentWeather(latittude, longitude);
            } catch (JSONException e) {
                Log.e("JSONError", "videoPlayer loadData", e);
            }
            return null;
        } else {
            return createFictionalJSONObject();
        }
    }

    private JSONObject createFictionalJSONObject(){
        String obj = "{\"coord\":{\"lon\":8.1213,\"lat\":52.6627},\"weather\":[{\"id\":800" +
                ",\"main\":\"Clouds\",\"description\":\"broken clouds\",\"icon\":\"04d\"}],\"base\":\"stations\"" +
                ",\"main\":{\"temp\":286.26,\"feels_like\":285.92,\"temp_min\":285.25,\"temp_max\":287.22,\"pressure\"" +
                ":1013,\"humidity\":88},\"visibility\":10000,\"wind\":{\"speed\":4.12,\"deg\":220},\"clouds\":{\"all\"" +
                ":75},\"dt\":1708005301,\"sys\":{\"type\":2,\"id\":2007385,\"country\":\"DE\",\"sunrise\":1707979541," +
                "\"sunset\":1708015075},\"timezone\":3600,\"id\":2936881,\"name\":\"Dinklage\",\"cod\":200}";
        try {
            return new JSONObject(obj);
        } catch (JSONException e){
            Log.e("JSONError", "VideoPlayer fict. obj", e);
            return null;
        }
    }
}
