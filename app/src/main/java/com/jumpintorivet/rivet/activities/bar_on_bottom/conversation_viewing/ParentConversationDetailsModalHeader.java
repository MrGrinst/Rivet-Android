package com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_viewing;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.models.ParentConversation;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ParentConversationDetailsModalHeader extends LinearLayout {
    @Bind(R.id.bar_on_bottom_parent_conversation_details_modal_header_headline)
    TextView headlineTextView;
    @Bind(R.id.bar_on_bottom_parent_conversation_details_modal_header_description)
    TextView descriptionTextView;

    public ParentConversationDetailsModalHeader(Context context) {
        super(context);
    }

    public ParentConversationDetailsModalHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ParentConversationDetailsModalHeader(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
    }

    public void setParentConversation(ParentConversation parentConversation) {
        headlineTextView.setText(parentConversation.getHeadline());
        descriptionTextView.setText(parentConversation.getDescription());
    }
}
