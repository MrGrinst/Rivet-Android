package com.jumpintorivet.rivet.activities.conversation_making;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.components.base.PathMaskableImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReceivedPrivateMessageTutorial extends FrameLayout {
    @Bind(R.id.conversation_making_received_private_message_tutorial_background_view)
    PathMaskableImageView backgroundView;

    public ReceivedPrivateMessageTutorial(Context context) {
        this(context, null);
    }

    public ReceivedPrivateMessageTutorial(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReceivedPrivateMessageTutorial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.conversation_making_received_private_message_tutorial_view, this);
        ButterKnife.bind(this);
    }

    public void setMask(Rect mask) {
        backgroundView.setMask(mask);
        invalidate();
    }
}
