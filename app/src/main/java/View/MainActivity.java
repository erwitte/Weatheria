package View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridLayout;

import com.example.weatheria.R;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background);

        GridLayout gridLayout = findViewById(R.id.grid_layout);

        LayoutManager l = new LayoutManager(gridLayout);
        l.initialStart();

        PlayerView playerView = findViewById(R.id.player_view);
        VideoPlayer t = new VideoPlayer(this, playerView);
        t.play();
        // Add the view to the GridLayout
        //initializePlayer();
    }

    private void initializePlayer() {
            ExoPlayer player = new ExoPlayer.Builder(this).build();

            // Bind the player to the view
            PlayerView playerView = findViewById(R.id.player_view);
            playerView.setPlayer(player);

            // Prepare the media source
            String videoUrl = "android.resource://" + getPackageName() + "/" + R.raw.rain;
            ; // Replace with your video URL or URI
            MediaItem mediaItem = MediaItem.fromUri(videoUrl);
            DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                    this, Util.getUserAgent(this, getString(R.string.app_name)));
            ProgressiveMediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(mediaItem);
            player.setRepeatMode(ExoPlayer.REPEAT_MODE_ONE);
            player.setMediaSource(mediaSource);
            player.prepare();
            player.play();
    }
}