package com.jumpintorivet.rivet.utils;


import android.app.Application;

import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.injection.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class StringUtil {
    private Application application;

    @Inject
    protected StringUtil(@ForApplication MyApplication application) {
        this.application = application;
    }

    public String getStringUsingResourceId(int resourceId) {
        return application.getResources().getString(resourceId);
    }
}
