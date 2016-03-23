package com.jumpintorivet.rivet.utils;

import android.content.Context;
import android.graphics.Paint;

import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.injection.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ColorUtil {
    private Paint paint;
    private Context context;

    @Inject
    protected ColorUtil(@ForApplication MyApplication context) {
        paint = new Paint();
        this.context = context;
    }

    public Paint getColorUsingResourceId(int resourceId) {
        paint.setColor(context.getResources().getColor(resourceId));
        paint.setPathEffect(null);
        paint.setAntiAlias(false);
        return paint;
    }
}