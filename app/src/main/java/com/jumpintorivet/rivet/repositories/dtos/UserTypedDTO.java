package com.jumpintorivet.rivet.repositories.dtos;

import com.google.gson.annotations.Expose;

public class UserTypedDTO {
    @Expose
    int participant_number;

    public int getParticipantNumber() {
        return participant_number;
    }

    public void setParticipantNumber(int participant_number) {
        this.participant_number = participant_number;
    }
}