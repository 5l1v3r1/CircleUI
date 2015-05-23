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

    public void draw(Canvas canvas, Rect bounds, float alpha) {
        super.draw(canvas, bounds, alpha);
        if (drawable == null) {
            return;
        }

        int inset = bounds.width() / 4;
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

}
