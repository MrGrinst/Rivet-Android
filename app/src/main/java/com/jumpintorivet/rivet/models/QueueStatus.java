package com.jumpintorivet.rivet.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class QueueStatus {
    @Expose
    @SerializedName("channel")
    String channel;
    @Expose
    @SerializedName("conversation_details")
    ConversationDetails conversationDetails;
    @Expose
    @SerializedName("is_in_queue")
    boolean isInQueue;
    @Expose
    @SerializedName("match_found")
    boolean matchFound;
    @Expose
    @SerializedName("parent_conversations")
    List<ParentConversation> parentConversations;

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public ConversationDetails getConversationDetails() {
        return conversationDetails;
    }

    public void setConversationDetails(ConversationDetails conversationDetails) {
        this.conversationDetails = conversationDetails;
    }

    public boolean isInQueue() {
        return isInQueue;
    }

    public void setIsInQueue(boolean isInQueue) {
        this.isInQueue = isInQueue;
    }

    public boolean isMatchFound() {
        return matchFound;
    }

    public void setMatchFound(boolean matchFound) {
        this.matchFound = matchFound;
    }

    public List<ParentConversation> getParentConversations() {
        return parentConversations;
    }

    public void setParentConversations(List<ParentConversation> parentConversations) {
        this.parentConversations = parentConversations;
    }
}