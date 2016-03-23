package com.jumpintorivet.rivet.utils;

import com.google.gson.Gson;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class JsonUtil {
    private Gson gson;

    @Inject
    public JsonUtil(Gson gson) {
        this.gson = gson;
    }

    public <T> T convertStringToObject(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }
}
