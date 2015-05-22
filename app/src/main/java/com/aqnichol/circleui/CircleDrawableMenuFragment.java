package com.aqnichol.circleui;

import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * Created by alex on 5/22/15.
 */
public class CircleDrawableMenuFragment extends CircleDrawableFragment {

    private CircleFragment[] options = null;

    public CircleDrawableMenuFragment() {
        super();
    }

    public boolean isMenu() {
        return true;
    }

    public CircleFragment[] getMenuOptions() {
        return options;
    }

    public void setMenuOptions(CircleFragment[] items) {
        options = items;
    }

}
