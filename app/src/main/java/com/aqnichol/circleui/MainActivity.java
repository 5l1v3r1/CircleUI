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
        CircleDrawableFragment f2 = new CircleDrawableFragment();
        f2.setBackgroundColor(Color.rgb(0xfb, 0x9f, 0x33));
        CircleDrawableFragment f3 = new CircleDrawableFragment();
        f3.setBackgroundColor(Color.rgb(0xdc, 0x1f, 0x28));
        CircleDrawableFragment f4 = new CircleDrawableFragment();
        f4.setBackgroundColor(Color.rgb(0x33, 0xa7, 0xd5));

        mainCircle.setMenuOptions(new CircleFragment[]{f1, f2, f3, f4});

        super.showMenu(mainCircle);
    }

}
