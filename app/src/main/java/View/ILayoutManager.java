package View;

import android.view.View;

import java.util.List;

public interface ILayoutManager {
    void updateLayout(List<View> newViews);
    void searchInitiated(String toSearch);
}
