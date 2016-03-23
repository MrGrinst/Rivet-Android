package com.jumpintorivet.rivet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import org.parceler.Parcel;

import java.io.Serializable;

@Parcel(analyze = ParentConversation.class, value = Parcel.Serialization.BEAN)
public class ParentConversation extends SugarRecord<ParentConversation> implements Serializable {
    @Expose
    @SerializedName("conversation_id")
    long conversationId;
    @Expose
    @SerializedName("headline")
    String headline;
    @Expose
    @SerializedName("description")
    String description;
    @Expose
    @SerializedName("participant_number")
    int participantNumber;

    Conversation conversation;

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getParticipantNumber() {
        return participantNumber;
    }

    public void setParticipantNumber(int participantNumber) {
        this.participantNumber = participantNumber;
    }
}
