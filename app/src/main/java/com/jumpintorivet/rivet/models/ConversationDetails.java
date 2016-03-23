package com.jumpintorivet.rivet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.Date;
import java.util.List;

@Parcel(analyze = ConversationDetails.class, value = Parcel.Serialization.BEAN)
public class ConversationDetails {
    @Expose
    @SerializedName("conversation_id")
    long conversationId;
    @Expose
    @SerializedName("end_time")
    Date endTime;
    @Expose
    @SerializedName("score")
    int score;
    @Expose
    @SerializedName("my_participant_number")
    int myParticipantNumber;
    @Expose
    @SerializedName("channel")
    String channel;
    @Expose
    @SerializedName("ended_by_participant_number")
    int endedByParticipantNumber;
    @Expose
    @SerializedName("description")
    String description;
    @Expose
    @SerializedName("parent_conversations")
    List<ParentConversation> parentConversations;
    @Expose
    @SerializedName("child_conversation_count")
    int childConversationCount;
    @Expose
    @SerializedName("picture_url")
    String pictureUrl;
    @Expose
    @SerializedName("my_current_vote")
    int myCurrentVote;
    @Expose
    @SerializedName("is_featured")
    boolean isFeatured;
    @Expose
    @SerializedName("headline")
    String headline;

    public ConversationDetails() {
        endTime = new Date(0);
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public boolean isActive() {
        return endTime == null;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getMyCurrentVote() {
        return myCurrentVote;
    }

    public void setMyCurrentVote(int myCurrentVote) {
        this.myCurrentVote = myCurrentVote;
    }

    public int getEndedByParticipantNumber() {
        return endedByParticipantNumber;
    }

    public void setEndedByParticipantNumber(int endedByParticipantNumber) {
        this.endedByParticipantNumber = endedByParticipantNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ParentConversation> getParentConversations() {
        return parentConversations;
    }

    public void setParentConversations(List<ParentConversation> parentConversations) {
        this.parentConversations = parentConversations;
    }

    public int getChildConversationCount() {
        return childConversationCount;
    }

    public void setChildConversationCount(int childConversationCount) {
        this.childConversationCount = childConversationCount;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getMyParticipantNumber() {
        return myParticipantNumber;
    }

    public void setMyParticipantNumber(int myParticipantNumber) {
        this.myParticipantNumber = myParticipantNumber;
    }

    public boolean isFeatured() {
        return isFeatured;
    }

    public void setIsFeatured(boolean isFeatured) {
        this.isFeatured = isFeatured;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }
}