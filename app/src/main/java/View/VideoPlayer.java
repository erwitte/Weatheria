package View;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.util.Log;

import com.google.android.exoplayer2.source.MediaSource;
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

import Controller.FileWriterReader;
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
        this.weatherFetcher = new WeatherFetcher(context);
    }

    public ExoPlayer getExoPlayer(){
        return this.exoPlayer;
    }

    public void play(){
        Log.i("esagfa", fileWriterReader.readCurrentWeather());
        if (hasInternet()){
            videoUrl = decideVideo();
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(context);

            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
            exoPlayer.setMediaSource(mediaSource);
            exoPlayer.prepare();
            exoPlayer.play();
        } else {
            videoUrl = "android.resource://" + context.getPackageName() + "/" + R.raw.clear;
            String userAgent = Util.getUserAgent(context, context.getString(R.string.app_name));
            DefaultHttpDataSource.Factory dataSourceFactory = new DefaultHttpDataSource.Factory()
                    .setUserAgent(userAgent);

            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            exoPlayer.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
            exoPlayer.setMediaSource(mediaSource);
            exoPlayer.prepare();
            exoPlayer.play();
        }
    }

    private boolean hasInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
        if (networkCapabilities == null) {
            return false;
        }
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED);
    }

    private String decideVideo(){
        JSONObject currentWeather = weatherFetcher.getCurrentWeather(52.2719595, 8.047635);
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
}
