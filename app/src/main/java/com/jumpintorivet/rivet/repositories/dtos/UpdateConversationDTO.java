package com.jumpintorivet.rivet.repositories.dtos;

import com.google.gson.annotations.Expose;

public class UpdateConversationDTO {
    @Expose
    public String status;

    public UpdateConversationDTO(String status) {
        this.status = status;
    }
}