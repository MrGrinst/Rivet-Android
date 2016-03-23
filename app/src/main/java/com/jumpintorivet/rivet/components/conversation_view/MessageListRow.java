package com.jumpintorivet.rivet.components.conversation_view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.components.base.MaskableTextView;
import com.jumpintorivet.rivet.components.base.RectMaskableImageView;
import com.jumpintorivet.rivet.models.Message;
import com.jumpintorivet.rivet.utils.ConstUtil;
import com.squareup.otto.Bus;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MessageListRow extends FrameLayout {
    @Bind(R.id.components_conversation_view_message_list_row_message_text_view)
    MaskableTextView messageTextView;
    @Bind(R.id.components_conversation_view_message_list_row_message_text_coverer)
    RectMaskableImageView nonPrivateMessageCoverer;
    @Inject
    ConstUtil constUtil;
    @Inject
    Bus bus;
    private Message message;
    private boolean isOnLeft;
    private boolean isPartnerView;

    public MessageListRow(Context context) {
        this(context, null);
    }

    public MessageListRow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MessageListRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((MyApplication) context.getApplicationContext()).inject(this);
        bus.register(this);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        messageTextView.setMaxWidth((int) (constUtil.getScreenWidth() * (2.0 / 3.0)));
        messageTextView.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ClipboardManager cManager = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData cData = ClipData.newPlainText("text", message.getText());
                cManager.setPrimaryClip(cData);
                Toast.makeText(getContext(), "Copied", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
    }

    public void setPrivacyMask(Rect privacyMask) {
        if (nonPrivateMessageCoverer != null) {
            if (!message.isPrivate()) {
                nonPrivateMessageCoverer.setMask(privacyMask);
                messageTextView.setMask(null);
            } else {
                nonPrivateMessageCoverer.setMask(null);
                messageTextView.setMask(privacyMask);
            }
        }
    }

    public void updatePrivacyMask() {
        if (!message.isPrivate()) {
            nonPrivateMessageCoverer.invalidate();
        } else {
            messageTextView.invalidate();
        }
    }

    public void setMessage(Message message, boolean isOnLeft, boolean isPartnerView) {
        this.isPartnerView = isPartnerView;
        this.message = message;
        this.isOnLeft = isOnLeft;
        if (messageTextView != null) {
            messageTextView.setVisibility(VISIBLE);
            nonPrivateMessageCoverer.setVisibility(VISIBLE);
            messageTextView.setText(message.getText());
            ((FrameLayout.LayoutParams) messageTextView.getLayoutParams()).gravity = isOnLeft ? Gravity.LEFT : Gravity.RIGHT;
            ((GradientDrawable) messageTextView.getBackground()).setColor(isOnLeft ? getContext().getResources().getColor(R.color.rivet_light_gray) : getContext().getResources().getColor(R.color.rivet_my_message_color));
            messageTextView.setTextColor(isOnLeft ? getContext().getResources().getColor(R.color.rivet_off_black) : getContext().getResources().getColor(android.R.color.white));
            messageTextView.setLinkTextColor(isOnLeft ? getContext().getResources().getColor(R.color.rivet_light_blue) : getContext().getResources().getColor(R.color.rivet_light_gray));
            if (!message.isPrivate()) {
                messageTextView.measure(MeasureSpec.makeMeasureSpec((int) (constUtil.getScreenWidth() * (2.0 / 3.0)), MeasureSpec.AT_MOST), MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
                nonPrivateMessageCoverer.getLayoutParams().width = messageTextView.getMeasuredWidth();
                nonPrivateMessageCoverer.getLayoutParams().height = messageTextView.getMeasuredHeight();
                ((FrameLayout.LayoutParams) nonPrivateMessageCoverer.getLayoutParams()).gravity = isOnLeft ? Gravity.LEFT : Gravity.RIGHT;
                ((GradientDrawable) nonPrivateMessageCoverer.getDrawable()).setColor(isOnLeft ? getContext().getResources().getColor(R.color.rivet_light_gray) : getContext().getResources().getColor(R.color.rivet_my_message_color));
                nonPrivateMessageCoverer.setVisibility(VISIBLE);
            } else {
                nonPrivateMessageCoverer.setVisibility(GONE);
            }
            invalidate();
        }
    }
}
