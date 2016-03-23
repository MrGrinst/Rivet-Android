package com.jumpintorivet.rivet.utils;

import android.content.res.Resources;

public class P {
    public static int convert(float original) {
        return (int) (Resources.getSystem().getDisplayMetrics().density * original + 0.5f);
    }
}
