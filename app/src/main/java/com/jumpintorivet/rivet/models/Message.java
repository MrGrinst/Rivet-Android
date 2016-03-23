package com.jumpintorivet.rivet.models;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.Date;

@Parcel(analyze = Message.class, value = Parcel.Serialization.BEAN)
public class Message extends SugarRecord<Message> implements Comparable, Serializable {
    @Expose
    @SerializedName("message_id")
    long messageId;
    @Expose
    @SerializedName("text")
    String text;
    @Expose
    @SerializedName("participant_number")
    int participantNumber;
    @Expose
    @SerializedName("recorded_at")
    Date timestamp;
    @Expose
    @SerializedName("is_private")
    boolean isPrivate;

    Conversation conversation;

    public Message() {
        timestamp = new Date(0);
    }

    public boolean isFromMe(int myParticipantNumber) {
        return myParticipantNumber == participantNumber;
    }

    public boolean isOnLeftWithMyParticipantNumber(int myParticipantNumber) {
        boolean messageIsFromMe = myParticipantNumber == participantNumber;
        boolean selfParticipated = myParticipantNumber != -1;
        return (selfParticipated && !messageIsFromMe)
                || (!selfParticipated && participantNumber == 1);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Message && ((Message) o).messageId == messageId;
    }

    @Override
    public int compareTo(@NonNull Object another) {
        if (another instanceof Message) {
            return timestamp.compareTo(((Message) another).timestamp);
        }
        return 0;
    }

    public long getMessageId() {
        return messageId;
    }

    public void setMessageId(long messageId) {
        this.messageId = messageId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getParticipantNumber() {
        return participantNumber;
    }

    public void setParticipantNumber(int participantNumber) {
        this.participantNumber = participantNumber;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setIsPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
