package com.jumpintorivet.rivet.utils;

import com.crashlytics.android.Crashlytics;
import com.jumpintorivet.rivet.BuildConfig;
import com.jumpintorivet.rivet.repositories.dtos.UserDTO;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class AuthenticationUtil {
    @Inject
    AppState appState;
    @Inject
    EventTrackingUtil eventTrackingUtil;

    public void saveNewUser(UserDTO newUser) {
        appState.setAuthToken(newUser.getAuth_token());
        appState.setUserId(newUser.getApp_user_id());
        eventTrackingUtil.setUserId(newUser.getApp_user_id());
        if (!BuildConfig.DEBUG) {
            Crashlytics.getInstance().core.setUserIdentifier(newUser.getApp_user_id() + "");
        }
    }

    public void resetCredentials() {
        appState.setAuthToken(null);
        appState.setUserId(-1);
    }
}