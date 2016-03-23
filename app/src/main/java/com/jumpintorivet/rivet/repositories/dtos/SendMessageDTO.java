package com.jumpintorivet.rivet.repositories.dtos;

import com.google.gson.annotations.Expose;

public class SendMessageDTO {
    @Expose
    String text;
    @Expose
    boolean is_private;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isIs_private() {

        return is_private;
    }

    public void setIs_private(boolean is_private) {
        this.is_private = is_private;
    }
}
