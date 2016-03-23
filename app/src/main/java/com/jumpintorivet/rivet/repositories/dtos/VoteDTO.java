package com.jumpintorivet.rivet.repositories.dtos;

import com.google.gson.annotations.Expose;

public class VoteDTO {
    @Expose
    public int value;

    public VoteDTO(int value) {
        this.value = value;
    }
}