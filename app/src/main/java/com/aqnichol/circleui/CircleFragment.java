package com.aqnichol.circleui;

import android.app.Fragment;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;

/**
 * A CircleFragment represents an option or submenu in a CircleActivity.
 */
public class CircleFragment extends Fragment {

    private Paint paint = new Paint();

    public CircleFragment() {
        paint.setAntiAlias(true);
    }

    public boolean isMenu() {
        return false;
    }

    public CircleFragment[] getMenuOptions() {
        return null;
    }

    public void draw(Canvas c, Rect b, float alpha) {
        c.drawCircle(b.exactCenterX(), b.exactCenterY(), (float)b.width() / 2, paint);
    }

    public int getBackgroundColor() {
        return paint.getColor();
    }

    public void setBackgroundColor(int backgroundColor) {
        paint.setColor(backgroundColor);
    }

}
