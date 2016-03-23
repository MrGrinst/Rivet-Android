package com.jumpintorivet.rivet.components.conversation_view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.OmniBar;
import com.jumpintorivet.rivet.components.base.NamedDrawable;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.squareup.otto.Bus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeaturedConversationHeaderRow extends LinearLayout {
    @Bind(R.id.components_conversation_view_featured_conversation_header_row_image)
    ImageView pictureView;
    @Bind(R.id.components_conversation_view_featured_conversation_header_row_headline)
    TextView headlineTextView;
    @Bind(R.id.components_conversation_view_featured_conversation_header_row_description)
    TextView descriptionTextView;
    @Inject
    Bus bus;

    public FeaturedConversationHeaderRow(Context context) {
        super(context);
    }

    public FeaturedConversationHeaderRow(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeaturedConversationHeaderRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ((MyApplication) getContext().getApplicationContext()).inject(this);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.components_conversation_view_featured_conversation_header_row_share)
    public void shareConversation() {
        bus.post(new OmniBar.ShowShareDialogEvent());
    }

    public void setConversationSummary(final ConversationSummary conversationSummary) {
        if (conversationSummary.getPicture() == null) {
            Drawable placeholder = getResources().getDrawable(R.drawable.placeholder_image, getContext().getTheme());
            conversationSummary.setPicture(new NamedDrawable(null, placeholder, "placeholder", getResources()));
            pictureView.setImageDrawable(placeholder);
        } else {
            pictureView.setImageDrawable(conversationSummary.getPicture().getNormalDrawable());
        }
        headlineTextView.setText(conversationSummary.getHeadline());
        descriptionTextView.setText(conversationSummary.getDescription());
        if (conversationSummary.getPicture().getName().equals("placeholder")) {
            Picasso.with(getContext())
                    .load(conversationSummary.getPictureUrl())
                    .placeholder(conversationSummary.getPicture().getNormalDrawable())
                    .error(conversationSummary.getPicture().getNormalDrawable())
                    .into(pictureView, new Callback() {
                        @Override
                        public void onSuccess() {
                            conversationSummary.setPicture(new NamedDrawable(null, pictureView.getDrawable(), conversationSummary.getPictureUrl(), getResources()));
                        }

                        @Override
                        public void onError() {
                        }
                    });
        }
    }
}
