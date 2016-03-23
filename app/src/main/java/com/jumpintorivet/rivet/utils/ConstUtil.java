package com.jumpintorivet.rivet.utils;

import android.app.Application;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.injection.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class ConstUtil {
    private Display display;
    private Integer screenWidth;
    private Integer screenHeight;
    private Integer statusBarHeight;
    private Application application;

    @Inject
    protected ConstUtil(@ForApplication MyApplication application) {
        this.application = application;
        WindowManager wm = (WindowManager) application.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
    }

    public Integer getScreenWidth() {
        if (screenWidth == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            screenWidth = metrics.widthPixels;
        }
        return screenWidth;
    }

    public Integer getScreenHeight() {
        if (screenHeight == null) {
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            screenHeight = metrics.heightPixels;
        }
        return screenHeight;
    }

    public Integer getStatusBarHeight() {
        if (statusBarHeight == null) {
            int resourceId = application.getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                statusBarHeight = application.getResources().getDimensionPixelSize(resourceId);
            }
        }
        return statusBarHeight;
    }
}
