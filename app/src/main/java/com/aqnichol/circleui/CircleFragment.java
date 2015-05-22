package com.aqnichol.circleui;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Rect;

/**
 * A CircleFragment represents an option or submenu in a CircleActivity.
 */
abstract public class CircleFragment extends Fragment {

    public CircleFragment() {
    }

    public boolean isMenu() {
        return false;
    }

    public CircleFragment[] getMenuOptions() {
        return null;
    }

    abstract public void draw(Canvas canvas, Rect bounds, float alpha);

}
