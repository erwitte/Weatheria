package View;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.GridLayout;

import com.example.weatheria.R;
import com.google.android.exoplayer2.ui.PlayerView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.background);

        GridLayout gridLayout = findViewById(R.id.grid_layout);

        LayoutManager layoutManager = new LayoutManager(gridLayout);
        layoutManager.initialStart();

        PlayerView playerView = findViewById(R.id.player_view);
        VideoPlayer videoPlayer = new VideoPlayer(this, playerView);
        videoPlayer.play();
    }
}