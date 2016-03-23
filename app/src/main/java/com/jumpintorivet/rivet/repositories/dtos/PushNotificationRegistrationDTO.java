package com.jumpintorivet.rivet.repositories.dtos;

import com.google.gson.annotations.Expose;

public class PushNotificationRegistrationDTO {
    @Expose
    String token;
    @Expose
    String device_type;

    public PushNotificationRegistrationDTO(String token, String device_type) {
        this.token = token;
        this.device_type = device_type;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getDevice_type() {
        return device_type;
    }

    public void setDevice_type(String device_type) {
        this.device_type = device_type;
    }
}