package com.jumpintorivet.rivet.repositories.dtos;

import com.google.gson.annotations.Expose;
import com.jumpintorivet.rivet.models.UserState;

public class UserDTO {
    @Expose
    long app_user_id;
    @Expose
    String auth_token;
    @Expose
    UserState user_state;

    public long getApp_user_id() {
        return app_user_id;
    }

    public void setApp_user_id(long app_user_id) {
        this.app_user_id = app_user_id;
    }

    public String getAuth_token() {
        return auth_token;
    }

    public void setAuth_token(String auth_token) {
        this.auth_token = auth_token;
    }

    public UserState getUser_state() {
        return user_state;
    }

    public void setUser_state(UserState user_state) {
        this.user_state = user_state;
    }
}
