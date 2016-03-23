package com.jumpintorivet.rivet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.jumpintorivet.rivet.components.base.NamedDrawable;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.parceler.Parcel;
import org.parceler.Transient;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

@Parcel(analyze = ConversationSummary.class, value = Parcel.Serialization.BEAN)
public class ConversationSummary extends SugarRecord<ConversationSummary> implements Serializable {
    @Expose
    @SerializedName("conversation_id")
    long conversationId;
    @Expose
    @SerializedName("message_count")
    int messageCount;
    @Expose
    @SerializedName("description")
    String description;
    @Expose
    @SerializedName("last_message_sent_by_me")
    String lastMessageSentByMe;
    @Expose
    @SerializedName("picture_url")
    String pictureUrl;
    @Expose
    @SerializedName("score")
    int score;
    @Expose
    @SerializedName("end_time")
    Date endTime;
    @Expose
    @SerializedName("my_current_vote")
    int myCurrentVote;
    @Expose
    @SerializedName("my_participant_number")
    int myParticipantNumber;
    @Expose
    @SerializedName("headline")
    String headline;
    @Ignore
    @Transient
    private transient NamedDrawable picture;

    String listType;

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public int getMessageCount() {
        return messageCount;
    }

    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLastMessageSentByMe() {
        return lastMessageSentByMe;
    }

    public void setLastMessageSentByMe(String lastMessageSentByMe) {
        this.lastMessageSentByMe = lastMessageSentByMe;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public int getMyParticipantNumber() {
        return myParticipantNumber;
    }

    public void setMyParticipantNumber(int myParticipantNumber) {
        this.myParticipantNumber = myParticipantNumber;
    }

    public int getMyCurrentVote() {
        return myCurrentVote;
    }

    public void setMyCurrentVote(int myCurrentVote) {
        this.myCurrentVote = myCurrentVote;
    }

    public String getListType() {
        return listType;
    }

    public void setListType(String listType) {
        this.listType = listType;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    @Transient
    public NamedDrawable getPicture() {
        return picture;
    }

    @Transient
    public void setPicture(NamedDrawable picture) {
        this.picture = picture;
    }

    public static final Comparator<ConversationSummary> NEW_COMPARATOR = new Comparator<ConversationSummary>() {
        @Override
        public int compare(ConversationSummary lhs, ConversationSummary rhs) {
            return rhs.getEndTime().compareTo(lhs.getEndTime());
        }
    };
}