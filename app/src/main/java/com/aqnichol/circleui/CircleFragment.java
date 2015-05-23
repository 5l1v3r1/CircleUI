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
    private String uniqueName;
    private static int uniqueIndex = 0;

    public CircleFragment() {
        paint.setAntiAlias(true);
        uniqueName = "CircleFragment_" + (++uniqueIndex);
    }

    public boolean isMenu() {
        return false;
    }

    public CircleFragment[] getMenuOptions() {
        return null;
    }

    public void draw(Canvas c, Rect b, float alpha) {
        paint.setAlpha(Math.round(alpha * 255.0f));
        c.drawCircle(b.exactCenterX(), b.exactCenterY(), (float)b.width() / 2, paint);
    }

    public String getUniqueName() {
        return uniqueName;
    }

    public int getBackgroundColor() {
        return paint.getColor();
    }

    public void setBackgroundColor(int backgroundColor) {
        paint.setColor(backgroundColor);
    }

}
