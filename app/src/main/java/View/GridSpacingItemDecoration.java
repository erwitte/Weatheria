package View;
// generiert mit Prompts "i can set gridlayout.params for a recycler view just as any other view?
//and how can i define the spaces between the columns?" and "rewrite that class so that it calculates inputted pixels as sp"
import android.content.Context;
import android.graphics.Rect;
import android.util.TypedValue;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

    private int spanCount; // Number of columns in the grid
    private int spacing; // Space in sp converted to pixels
    private boolean includeEdge; // Whether to include the edge spacing

    public GridSpacingItemDecoration(Context context, int spanCount, int spacingInSp, boolean includeEdge) {
        this.spanCount = spanCount;
        this.spacing = convertSpToPx(context, spacingInSp); // Convert SP to PX
        this.includeEdge = includeEdge;
    }

    private int convertSpToPx(Context context, int sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view); // Item position
        int column = position % spanCount; // Item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount;
            outRect.right = (column + 1) * spacing / spanCount;

            if (position < spanCount) { // Top edge
                outRect.top = spacing;
            }
            outRect.bottom = spacing; // Item bottom
        } else {
            outRect.left = column * spacing / spanCount;
            outRect.right = spacing - (column + 1) * spacing / spanCount;
            if (position >= spanCount) {
                outRect.top = spacing; // Item top
            }
        }
    }
}
//generiert ende

