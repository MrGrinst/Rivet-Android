package com.jumpintorivet.rivet.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.injection.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class RivetPreferences {
    private static final String HAS_SLID_PRIVACY_MASK_LEFT = "hasSlidePrivacyMaskLeft";
    private static final String IS_PRIVACY_MASK_ON_LEFT = "IS_PRIVACY_MASK_ON_LEFT";
    private static final String USER_ID = "userId";
    private static final String AUTH_TOKEN = "authToken";
    private static final String ACTIVE_CONVERSATION_ID = "activeConversationId";
    private static final String CONVERSATION_MODE = "conversationMode";
    private static final String WAIT_FOR_MATCH_CHANNEL = "waitForMatchChannel";
    private SharedPreferences sharedPreferences;

    @Inject
    public RivetPreferences(@ForApplication MyApplication application) {
        sharedPreferences = application.getSharedPreferences(application.getPackageName(), Context.MODE_PRIVATE);
    }

    public long getUserId() {
        return sharedPreferences.getLong(USER_ID, -1);
    }

    public void setUserId(long userId) {
        sharedPreferences.edit().putLong(USER_ID, userId).apply();
    }

    public String getAuthToken() {
        return sharedPreferences.getString(AUTH_TOKEN, null);
    }

    public void setAuthToken(String authToken) {
        sharedPreferences.edit().putString(AUTH_TOKEN, authToken).apply();
    }

    public String getConversationMode() {
        return sharedPreferences.getString(CONVERSATION_MODE, null);
    }

    public void setConversationMode(String conversationMode) {
        sharedPreferences.edit().putString(CONVERSATION_MODE, conversationMode).apply();
    }

    public long getActiveConversationId() {
        return sharedPreferences.getLong(ACTIVE_CONVERSATION_ID, -1);
    }

    public void setActiveConversationId(long conversationId) {
        sharedPreferences.edit().putLong(ACTIVE_CONVERSATION_ID, conversationId).apply();
    }

    public boolean hasSlidPrivacyMaskLeft() {
        return sharedPreferences.getBoolean(HAS_SLID_PRIVACY_MASK_LEFT, false);
    }

    public void setHasSlidPrivacyMaskLeft(boolean hasSlidPrivacyMaskLeft) {
        sharedPreferences.edit().putBoolean(HAS_SLID_PRIVACY_MASK_LEFT, hasSlidPrivacyMaskLeft).apply();
    }

    public String getWaitForMatchChannel() {
        return sharedPreferences.getString(WAIT_FOR_MATCH_CHANNEL, null);
    }

    public void setWaitForMatchChannel(String waitForMatchChannel) {
        sharedPreferences.edit().putString(WAIT_FOR_MATCH_CHANNEL, waitForMatchChannel).apply();
    }

    public boolean isPrivacyMaskOnLeft() {
        return sharedPreferences.getBoolean(IS_PRIVACY_MASK_ON_LEFT, false);
    }

    public void setIsPrivacyMaskOnLeft(boolean isPrivacyMaskOnLeft) {
        sharedPreferences.edit().putBoolean(IS_PRIVACY_MASK_ON_LEFT, isPrivacyMaskOnLeft).apply();
    }
}
