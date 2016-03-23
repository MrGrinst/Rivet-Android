package com.jumpintorivet.rivet.components.conversation_view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.models.Conversation;
import com.jumpintorivet.rivet.models.ParentConversation;
import com.jumpintorivet.rivet.utils.P;
import com.jumpintorivet.rivet.utils.StringUtil;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ParentConversationListRow extends LinearLayout {
    @Bind(R.id.components_conversation_list_view_conversation_parent_conversation_list_row_now_viewing_text)
    TextView nowViewingTextView;
    @Bind(R.id.components_conversation_list_view_conversation_parent_conversation_list_row_first_parent_introduction)
    TextView firstParentIntroduction;
    @Bind(R.id.components_conversation_list_view_conversation_parent_conversation_list_row_first_parent_box)
    LinearLayout firstParentBox;
    @Bind(R.id.components_conversation_list_view_conversation_parent_conversation_list_row_first_parent_headline)
    TextView firstParentHeadline;
    @Bind(R.id.components_conversation_list_view_conversation_parent_conversation_list_row_second_parent_introduction)
    TextView secondParentIntroduction;
    @Bind(R.id.components_conversation_list_view_conversation_parent_conversation_list_row_second_parent_box)
    LinearLayout secondParentBox;
    @Bind(R.id.components_conversation_list_view_conversation_parent_conversation_list_row_second_parent_headline)
    TextView secondParentHeadline;
    @Inject
    Bus bus;

    private Conversation conversation;

    public ParentConversationListRow(Context context) {
        this(context, null);
    }

    public ParentConversationListRow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ParentConversationListRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((MyApplication) context.getApplicationContext()).inject(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void setConversation(Conversation conversation) {
        this.conversation = conversation;
        secondParentIntroduction.setVisibility(GONE);
        secondParentBox.setVisibility(GONE);
        firstParentIntroduction.setVisibility(GONE);
        firstParentBox.setVisibility(GONE);
        if (conversation.getParentConversations() != null && !conversation.getParentConversations().isEmpty()) {
            if (conversation.isActive()) {
                if (conversation.getParentConversations().size() == 1) {
                    ParentConversation parentConversation = conversation.getParentConversations().get(0);
                    firstParentIntroduction.setVisibility(VISIBLE);
                    firstParentBox.setVisibility(VISIBLE);
                    firstParentHeadline.setText(parentConversation.getHeadline());
                    if (parentConversation.getParticipantNumber() == -1) {
                        firstParentIntroduction.setText(R.string.have_you_seen_the_featured_conversation);
                    } else if (parentConversation.getParticipantNumber() == conversation.getMyParticipantNumber()) {
                        firstParentIntroduction.setText(R.string.talk_about_the_conversation_you_recently_read);
                    } else {
                        firstParentIntroduction.setText(R.string.the_other_user_recently_read_this_featured_conversation);
                    }
                } else if (conversation.getParentConversations().get(0).getConversationId() == conversation.getParentConversations().get(1).getConversationId()) {
                    ParentConversation parentConversation = conversation.getParentConversations().get(0);
                    firstParentIntroduction.setVisibility(VISIBLE);
                    firstParentBox.setVisibility(VISIBLE);
                    firstParentHeadline.setText(parentConversation.getHeadline());
                    firstParentIntroduction.setText(R.string.you_both_recently_read);
                } else {
                    ParentConversation firstParentConversation = conversation.getParentConversations().get(0);
                    ParentConversation secondParentConversation = conversation.getParentConversations().get(1);
                    firstParentIntroduction.setVisibility(VISIBLE);
                    firstParentBox.setVisibility(VISIBLE);
                    secondParentIntroduction.setVisibility(VISIBLE);
                    secondParentBox.setVisibility(VISIBLE);
                    firstParentHeadline.setText(firstParentConversation.getHeadline());
                    secondParentHeadline.setText(secondParentConversation.getHeadline());
                    if (firstParentConversation.getParticipantNumber() == conversation.getMyParticipantNumber()) {
                        firstParentIntroduction.setText(R.string.talk_about_the_conversation_you_recently_read);
                        if (secondParentConversation.getParticipantNumber() == -1 || secondParentConversation.getParticipantNumber() == conversation.getMyParticipantNumber()) {
                            secondParentIntroduction.setText(R.string.since_they_recently_talked_about_this);
                        } else {
                            secondParentIntroduction.setText(R.string.the_other_user_recently_read_this_featured_conversation);
                        }
                    } else {
                        firstParentIntroduction.setText(R.string.the_other_user_recently_read_this_featured_conversation);
                        if (secondParentConversation.getParticipantNumber() == -1 || secondParentConversation.getParticipantNumber() != conversation.getMyParticipantNumber()) {
                            secondParentIntroduction.setText(R.string.since_you_recently_talked_about_this);
                        } else {
                            secondParentIntroduction.setText(R.string.talk_about_the_conversation_you_recently_read);
                        }
                    }
                }
            } else {
                if (conversation.getParentConversations().size() == 1 || (conversation.getParentConversations().get(0).getConversationId() == conversation.getParentConversations().get(1).getConversationId())) {
                    ParentConversation parentConversation = conversation.getParentConversations().get(0);
                    firstParentIntroduction.setVisibility(VISIBLE);
                    firstParentBox.setVisibility(VISIBLE);
                    firstParentHeadline.setText(parentConversation.getHeadline());
                    firstParentIntroduction.setText(R.string.featured_conversation_topic);
                } else {
                    ParentConversation firstParentConversation = conversation.getParentConversations().get(0);
                    ParentConversation secondParentConversation = conversation.getParentConversations().get(1);
                    firstParentIntroduction.setVisibility(VISIBLE);
                    firstParentBox.setVisibility(VISIBLE);
                    secondParentIntroduction.setVisibility(VISIBLE);
                    secondParentBox.setVisibility(VISIBLE);
                    firstParentHeadline.setText(firstParentConversation.getHeadline());
                    firstParentIntroduction.setText(R.string.featured_conversation_topic);
                    secondParentHeadline.setText(secondParentConversation.getHeadline());
                    secondParentIntroduction.setText(R.string.another_topic);
                }
            }
        }
        if (conversation.getDescription() != null) {
            nowViewingTextView.setText(conversation.getDescription());
            nowViewingTextView.setPadding(0, P.convert(12), 0, P.convert(8));
        } else {
            if (conversation.didSelfParticipate()) {
                if (conversation.isActive()) {
                    nowViewingTextView.setText(R.string.now_talking_text_participant);
                } else {
                    nowViewingTextView.setText(R.string.now_viewing_text_participant);
                }
            } else {
                nowViewingTextView.setText(R.string.now_talking_text_viewer);
            }
            nowViewingTextView.setPadding(0, 0, 0, 0);
        }
    }

    @OnClick(R.id.components_conversation_list_view_conversation_parent_conversation_list_row_first_parent_box)
    public void tappedFirstConversation() {
        bus.post(new ParentConversationTappedEvent(conversation.getParentConversations().get(0)));
    }

    @OnClick(R.id.components_conversation_list_view_conversation_parent_conversation_list_row_second_parent_box)
    public void tappedSecondConversation() {
        bus.post(new ParentConversationTappedEvent(conversation.getParentConversations().get(1)));
    }

    public static class ParentConversationTappedEvent {
        public final ParentConversation parentConversation;

        public ParentConversationTappedEvent(ParentConversation parentConversation) {
            this.parentConversation = parentConversation;
        }
    }
}