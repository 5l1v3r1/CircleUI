package com.aqnichol.circleui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A CircleImageFragment is a CircleFragment with an image.
 */
public class CircleDrawableFragment extends CircleFragment {

    private Drawable drawable = null;
    private Paint paint = new Paint();

    public void draw(Canvas canvas, Rect bounds, float alpha) {
        canvas.drawCircle(bounds.exactCenterX(), bounds.exactCenterY(), (float)bounds.width() / 2,
                paint);
        if (drawable == null) {
            return;
        }
        int inset = Math.round((float)bounds.width() * (1.0f - 1.0f/(float)Math.sqrt(2.0f)) / 2);
        bounds.inset(inset, inset);
        drawable.setBounds(bounds);
        drawable.setAlpha(Math.round(alpha * 255.0f));
        drawable.draw(canvas);
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getBackgroundColor() {
        return paint.getColor();
    }

    public void setBackgroundColor(int backgroundColor) {
        paint.setColor(backgroundColor);
    }

}
