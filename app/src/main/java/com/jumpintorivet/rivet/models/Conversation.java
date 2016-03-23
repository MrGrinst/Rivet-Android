package com.jumpintorivet.rivet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.orm.SugarRecord;
import com.orm.dsl.Ignore;

import org.parceler.Parcel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Parcel(analyze = Conversation.class, value = Parcel.Serialization.BEAN)
public class Conversation extends SugarRecord<Conversation> implements Serializable {
    @Expose
    @SerializedName("conversation_id")
    long conversationId;
    @Expose
    @SerializedName("end_time")
    Date endTime;
    @Expose
    @SerializedName("is_deleted")
    boolean isDeleted;
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
    @SerializedName("description")
    String description;
    @Ignore
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
    @SerializedName("ended_by_participant_number")
    int endedByParticipantNumber;
    @Expose
    @SerializedName("my_current_vote")
    int myCurrentVote;
    @Expose
    @SerializedName("is_featured")
    boolean isFeatured;
    @Expose
    @SerializedName("headline")
    String headline;
    @Expose
    @SerializedName("otherUserTyping")
    boolean otherUserTyping;
    @Ignore
    @Expose
    @SerializedName("messages")
    List<Message> messages;

    public Conversation() {
        endTime = new Date(0);
    }

    public Conversation(ConversationDetails conversationDetails, List<Message> messages) {
        conversationId = conversationDetails.conversationId;
        myParticipantNumber = conversationDetails.myParticipantNumber;
        channel = conversationDetails.channel;
        isFeatured = conversationDetails.isFeatured;
        headline = conversationDetails.headline;
        description = conversationDetails.description;
        parentConversations = conversationDetails.parentConversations;
        childConversationCount = conversationDetails.childConversationCount;
        pictureUrl = conversationDetails.pictureUrl;
        endTime = new Date(0);
        updateWithConversationDetailsAndMessages(conversationDetails, messages);
    }

    public void updateWithConversationDetailsAndMessages(ConversationDetails conversationDetails, List<Message> messages) {
        setEndTime(conversationDetails.endTime);
        score = conversationDetails.score;
        endedByParticipantNumber = conversationDetails.endedByParticipantNumber;
        myCurrentVote = conversationDetails.myCurrentVote;
        if (messages != null) {
            for (Message m : messages) {
                if (!getMessages().contains(m)) {
                    this.messages.add(m);
                }
            }
        }
        Collections.sort(getMessages());
    }

    public boolean didSelfParticipate() {
        return myParticipantNumber != -1;
    }

    public boolean isActive() {
        return endTime.getTime() == 0;
    }

    @Override
    public void save() {
        super.save();
        if (messages != null) {
            for (Message m : messages) {
                m.conversation = this;
            }
            Message.saveInTx(messages);
        }
        if (parentConversations != null) {
            for (ParentConversation c : parentConversations) {
                c.conversation = this;
            }
            ParentConversation.saveInTx(parentConversations);
        }
    }

    @Override
    public void delete() {
        Message.deleteAll(Message.class, "conversation = ?", getId() + "");
        ParentConversation.deleteAll(ParentConversation.class, "conversation = ?", getId() + "");
        super.delete();
    }

    public List<Message> getMessages() {
        if (messages == null) {
            messages = Message.find(Message.class, "conversation = ?", "" + getId());
            if (messages == null) {
                return new ArrayList<>();
            }
        }
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
        if (endTime == null) {
            this.endTime = new Date(0);
        }
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getMyParticipantNumber() {
        return myParticipantNumber;
    }

    public void setMyParticipantNumber(int myParticipantNumber) {
        this.myParticipantNumber = myParticipantNumber;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<ParentConversation> getParentConversations() {
        if (parentConversations == null) {
            parentConversations = ParentConversation.find(ParentConversation.class, "conversation = ?", "" + getId());
            if (parentConversations == null) {
                return new ArrayList<>();
            }
        }
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

    public int getEndedByParticipantNumber() {
        return endedByParticipantNumber;
    }

    public void setEndedByParticipantNumber(int endedByParticipantNumber) {
        this.endedByParticipantNumber = endedByParticipantNumber;
    }

    public int getMyCurrentVote() {
        return myCurrentVote;
    }

    public void setMyCurrentVote(int myCurrentVote) {
        this.myCurrentVote = myCurrentVote;
    }

    public boolean isOtherUserTyping() {
        return otherUserTyping;
    }

    public void setOtherUserTyping(boolean otherUserTyping) {
        this.otherUserTyping = otherUserTyping;
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