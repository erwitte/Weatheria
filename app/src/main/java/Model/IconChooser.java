package Model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.util.Log;
import android.widget.ImageView;

import com.caverock.androidsvg.SVG;
import com.example.weatheria.R;

/**
 * A utility class that selects and sets weather icons based on weather condition IDs.
 * This class uses SVG images for high-quality scalable icons representing different weather conditions.
 */
public class IconChooser {

    private final Context context;

    /**
     * Constructs an IconChooser with the specified application context.
     * @param context The current application context used to access resources.
     */
    public IconChooser(Context context){
        this.context = context;
    }

    /**
     * Chooses and sets an icon based on the weather condition ID.
     * Maps a range of weather condition IDs to specific SVG icons representing various weather states.
     * @param id The weather condition ID.
     * @return An ImageView containing the selected icon as its drawable.
     */
    public ImageView chooseIcon(int id){
        ImageView icon = new ImageView(context);
        int resource = 0;
        if (id > 199 && id < 300){
            resource = R.raw.thunderstormsvg;
        } else if (id > 299 && id < 400) {
            resource = R.raw.drizzlesvg;
        } else if (id > 499 && id < 600) {
            resource = R.raw.rainsvg;
        } else if (id > 599 && id < 700) {
            resource = R.raw.snowsvg;
        } else if (id > 699 && id < 800) {
            resource = R.raw.mistsvg;
        } else if (id == 800) {
            resource = R.raw.clearsvg;
        } else if (id > 800 && id < 900) {
            resource = R.raw.cloudysvg;
        }

        icon.setImageDrawable(convertSvgToDrawable(resource));
        return icon;
    }

    /**
     * Converts an SVG resource to a Drawable that can be used in an ImageView.
     * This method allows the application to use scalable vector graphics as image icons.
     * @param svgResource The resource ID of the SVG to convert.
     * @return A Drawable representing the SVG, or null if an error occurs.
     */
    private Drawable convertSvgToDrawable(int svgResource) {
        try {
            SVG svg = SVG.getFromResource(context, svgResource);
            return new PictureDrawable(svg.renderToPicture());
        } catch (Exception e) {
            Log.e("SVGError", "Loading SVG resource failed.", e);
            return null;
        }
    }
}
