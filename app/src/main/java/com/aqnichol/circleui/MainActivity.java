package com.aqnichol.circleui;

import android.graphics.Color;
import android.os.Bundle;

public class MainActivity extends CircleActivity {

    public void onCreate(Bundle b) {
        super.onCreate(b);

        CircleDrawableMenuFragment mainCircle = new CircleDrawableMenuFragment();
        mainCircle.setBackgroundColor(Color.rgb(0xa9, 0x61, 0xd2));
        mainCircle.setDrawable(getDrawable(R.drawable.house));

        CircleDrawableFragment f1 = new CircleDrawableFragment();
        f1.setBackgroundColor(Color.rgb(0x81, 0xad, 0x2a));
        f1.setDrawable(getDrawable(R.drawable.flaticon1));

        CircleDrawableFragment f2 = new CircleDrawableFragment();
        f2.setBackgroundColor(Color.rgb(0xfb, 0x9f, 0x33));
        f2.setDrawable(getDrawable(R.drawable.flaticon2));

        CircleDrawableFragment f3 = new CircleDrawableFragment();
        f3.setBackgroundColor(Color.rgb(0xdc, 0x1f, 0x28));
        f3.setDrawable(getDrawable(R.drawable.flaticon3));

        CircleDrawableMenuFragment f4 = new CircleDrawableMenuFragment();
        f4.setBackgroundColor(Color.rgb(0x33, 0xa7, 0xd5));
        f4.setDrawable(getDrawable(R.drawable.flaticon4));

        CircleDrawableFragment f5 = new CircleDrawableFragment();
        f5.setBackgroundColor(Color.rgb(0x81, 0xad, 0x2a));
        f5.setDrawable(getDrawable(R.drawable.flaticon5));

        mainCircle.setMenuOptions(new CircleFragment[]{f1, f2, f3, f4, f5});

        CircleDrawableFragment f6 = new CircleDrawableFragment();
        f6.setBackgroundColor(Color.rgb(0xfb, 0x9f, 0x33));
        f6.setDrawable(getDrawable(R.drawable.flaticon6));

        CircleDrawableFragment f7 = new CircleDrawableFragment();
        f7.setBackgroundColor(Color.rgb(0xdc, 0x1f, 0x28));
        f7.setDrawable(getDrawable(R.drawable.flaticon7));

        CircleDrawableFragment f8 = new CircleDrawableFragment();
        f8.setBackgroundColor(Color.rgb(0x81, 0xad, 0x2a));
        f8.setDrawable(getDrawable(R.drawable.flaticon8));

        f4.setMenuOptions(new CircleFragment[]{f6, f7, f8});

        super.showMenu(mainCircle);
    }

}
