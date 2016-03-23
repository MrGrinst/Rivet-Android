package com.jumpintorivet.rivet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import org.parceler.Parcel;

@Parcel(analyze = UserProfile.class, value = Parcel.Serialization.BEAN)
public class UserProfile extends SugarRecord<UserProfile> {
    @Expose
    @SerializedName("conversations_count")
    int conversationCount;
    @Expose
    @SerializedName("conversations_total_score")
    int conversationsScore;
    @Expose
    @SerializedName("seconds_in_conversation")
    int secondsInConversation;
    @Expose
    @SerializedName("median_messages_per_conversation")
    int medianMessagesPerConversation;

    public int getConversationCount() {
        return conversationCount;
    }

    public void setConversationCount(int conversationCount) {
        this.conversationCount = conversationCount;
    }

    public int getConversationsScore() {
        return conversationsScore;
    }

    public void setConversationsScore(int conversationsScore) {
        this.conversationsScore = conversationsScore;
    }

    public int getSecondsInConversation() {
        return secondsInConversation;
    }

    public void setSecondsInConversation(int secondsInConversation) {
        this.secondsInConversation = secondsInConversation;
    }

    public int getMedianMessagesPerConversation() {
        return medianMessagesPerConversation;
    }

    public void setMedianMessagesPerConversation(int medianMessagesPerConversation) {
        this.medianMessagesPerConversation = medianMessagesPerConversation;
    }
}
