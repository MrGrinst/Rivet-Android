package com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_list;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.activities.bar_on_bottom.ListFragment;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.base.NamedDrawable;
import com.jumpintorivet.rivet.models.ConversationSummary;
import com.jumpintorivet.rivet.utils.ConstUtil;
import com.jumpintorivet.rivet.utils.P;
import com.squareup.otto.Bus;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FeaturedListCell extends LinearLayout {
    @Bind(R.id.bar_on_bottom_list_featured_conversation_list_fragment_cell_card_view)
    LinearLayout cardView;
    @Bind(R.id.bar_on_bottom_list_featured_conversation_list_fragment_cell_image)
    ImageView pictureView;
    @Bind(R.id.bar_on_bottom_list_featured_conversation_list_fragment_cell_headline)
    TextView headlineTextView;
    @Bind(R.id.bar_on_bottom_list_featured_conversation_list_fragment_cell_description)
    TextView descriptionTextView;
    @Bind(R.id.bar_on_bottom_list_featured_conversation_list_fragment_cell_timestamp)
    TextView timestampTextView;
    @Bind(R.id.bar_on_bottom_list_featured_conversation_list_fragment_cell_message_count)
    TextView messageCountTextView;
    @Inject
    Bus bus;
    @Inject
    ConstUtil constUtil;

    private ConversationSummary conversation;
    private SimpleDateFormat formatter;

    public FeaturedListCell(Context context) {
        super(context);
    }

    public FeaturedListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FeaturedListCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ((MyApplication) getContext().getApplicationContext()).inject(this);
        ButterKnife.bind(this);
        formatter = new SimpleDateFormat("M/d/yy", Locale.US);
        formatter.setTimeZone(TimeZone.getDefault());
        bus.register(this);
        LayoutParams cardViewParams = (LinearLayout.LayoutParams) cardView.getLayoutParams();
        cardViewParams.topMargin = (int) (Math.max(constUtil.getScreenHeight() - P.convert(500), P.convert(20)) / 2.0);
        cardViewParams.bottomMargin = (int) (Math.max(constUtil.getScreenHeight() - P.convert(500), P.convert(20)) / 2.0);
        cardView.setLayoutParams(cardViewParams);
        cardView.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
        pictureView.setMaxHeight((int) ((constUtil.getScreenHeight() - cardViewParams.topMargin - cardViewParams.bottomMargin - P.convert(100)) / 2.0));
        watchNumberOfLines();
    }

    @OnClick(R.id.bar_on_bottom_list_featured_conversation_list_fragment_cell_card_view)
    public void cellClicked() {
        bus.post(new ListFragment.ClickedOnFeaturedConversationInListEvent(conversation));
    }

    public void setConversation(final ConversationSummary conversation) {
        this.conversation = conversation;
        headlineTextView.setText(conversation.getHeadline());
        descriptionTextView.setText(conversation.getDescription());
        if (conversation.getMessageCount() == 1) {
            messageCountTextView.setText(getResources().getString(R.string.number_of_message, 1));
        } else {
            messageCountTextView.setText(getResources().getString(R.string.number_of_messages, conversation.getMessageCount()));
        }
        timestampTextView.setText(formatter.format(conversation.getEndTime()));
        Drawable placeholder = ContextCompat.getDrawable(getContext(), R.drawable.placeholder_image);
        conversation.setPicture(new NamedDrawable(placeholder, null, "placeholder", getResources()));
        Picasso.with(getContext())
                .load(conversation.getPictureUrl())
                .placeholder(placeholder)
                .error(placeholder)
                .into(pictureView, new Callback() {
                    @Override
                    public void onSuccess() {
                        conversation.setPicture(new NamedDrawable(pictureView.getDrawable(), null, conversation.getPictureUrl(), getResources()));
                    }

                    @Override
                    public void onError() {
                    }
                });
    }

    private void watchNumberOfLines() {
        ViewTreeObserver observer = descriptionTextView.getViewTreeObserver();
        observer.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int maxLines = descriptionTextView.getHeight() / descriptionTextView.getLineHeight();
                descriptionTextView.setMaxLines(maxLines);
            }
        });
    }
}
