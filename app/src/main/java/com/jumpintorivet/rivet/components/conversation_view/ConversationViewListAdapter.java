package com.jumpintorivet.rivet.components.conversation_view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.models.Message;

public class ConversationViewListAdapter extends BaseAdapter {
    private static final int VIEW_TYPE_PROMPT = 0;
    private static final int VIEW_TYPE_TYPING = 1;
    private static final int VIEW_TYPE_CONVERSATION_ENDED = 2;
    private static final int VIEW_TYPE_MESSAGE = 3;
    private static final int VIEW_TYPE_FEATURED_CONVERSATION_HEADER = 4;
    private static final int VIEW_TYPE_FEATURED_CONVERSATION_FOOTER = 5;

    private Conversation conversation;
    private ConversationSummary featuredConversationSummary;
    private LayoutInflater inflater;
    private boolean isPartnerView;
    private PrivacyMask privacyMask;

    public ConversationViewListAdapter(LayoutInflater inflater, Conversation conversation, ConversationSummary featuredConversationSummary, boolean isPartnerView, PrivacyMask privacyMask) {
        this.conversation = conversation;
        this.inflater = inflater;
        this.isPartnerView = isPartnerView;
        this.featuredConversationSummary = featuredConversationSummary;
        this.privacyMask = privacyMask;
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
    }

    @Override
    public int getCount() {
        if (conversation != null) {
            if (!conversation.isActive() || conversation.isOtherUserTyping()) {
                return (conversation.getMessages() == null ? 0 : conversation.getMessages().size()) + 2;
            }
            return (conversation.getMessages() == null ? 0 : conversation.getMessages().size()) + 1;
        } else if (featuredConversationSummary != null) {
            return 1;
        }
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (featuredConversationSummary != null || conversation != null) {
            switch (getItemViewType(position)) {
                case VIEW_TYPE_PROMPT:
                    if (convertView == null || !(convertView instanceof ParentConversationListRow)) {
                        convertView = inflater.inflate(R.layout.components_conversation_view_conversation_parent_conversation_list_row, null);
                    }
                    ParentConversationListRow cvplr = (ParentConversationListRow) convertView;
                    cvplr.setConversation(conversation);
                    break;
                case VIEW_TYPE_TYPING:
                    if (convertView == null || !(convertView instanceof TypingBubbleListRow)) {
                        convertView = inflater.inflate(R.layout.components_conversation_view_typing_bubble_list_row, null);
                    }
                    TypingBubbleListRow tblr = (TypingBubbleListRow) convertView;
                    tblr.resetColor();
                    break;
                case VIEW_TYPE_CONVERSATION_ENDED:
                    if (convertView == null || !(convertView instanceof ConversationEndedListRow)) {
                        convertView = inflater.inflate(R.layout.components_conversation_view_conversation_ended_list_row, null);
                    }
                    ConversationEndedListRow celr = (ConversationEndedListRow) convertView;
                    celr.setConversation(conversation, isPartnerView);
                    break;
                case VIEW_TYPE_MESSAGE:
                    if (convertView == null || !(convertView instanceof MessageListRow)) {
                        convertView = inflater.inflate(R.layout.components_conversation_view_message_list_row, null);
                    }
                    MessageListRow cmlr = (MessageListRow) convertView;
                    Message message = conversation.getMessages().get(position - 1);
                    cmlr.setMessage(message, message.isOnLeftWithMyParticipantNumber(conversation.getMyParticipantNumber()), isPartnerView);
                    privacyMask.addMaskToCell(cmlr);
                    break;
                case VIEW_TYPE_FEATURED_CONVERSATION_HEADER:
                    if (convertView == null || !(convertView instanceof FeaturedConversationHeaderRow)) {
                        convertView = inflater.inflate(R.layout.components_conversation_view_featured_conversation_header_row, null);
                    }
                    FeaturedConversationHeaderRow fchr = (FeaturedConversationHeaderRow) convertView;
                    fchr.setConversationSummary(featuredConversationSummary);
                    break;
                case VIEW_TYPE_FEATURED_CONVERSATION_FOOTER:
                    if (convertView == null || !(convertView instanceof FeaturedConversationFooterRow)) {
                        convertView = inflater.inflate(R.layout.components_conversation_view_featured_conversation_footer_row, null);
                    }
                    FeaturedConversationFooterRow fcfr = (FeaturedConversationFooterRow) convertView;
                    fcfr.setConversation(conversation);
                    break;
            }
        }
        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            if (featuredConversationSummary != null) {
                return VIEW_TYPE_FEATURED_CONVERSATION_HEADER;
            } else {
                return VIEW_TYPE_PROMPT;
            }
        } else if (position >= (conversation.getMessages() == null ? 0 : conversation.getMessages().size()) + 1) {
            if (conversation.isActive()) {
                return VIEW_TYPE_TYPING;
            } else if (conversation.isFeatured()) {
                return VIEW_TYPE_FEATURED_CONVERSATION_FOOTER;
            } else {
                return VIEW_TYPE_CONVERSATION_ENDED;
            }
        } else {
            return VIEW_TYPE_MESSAGE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 6;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}