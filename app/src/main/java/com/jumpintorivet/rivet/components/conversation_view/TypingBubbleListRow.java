package com.jumpintorivet.rivet.components.conversation_view;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class TypingBubbleListRow extends LinearLayout {
    @Bind(R.id.components_conversation_view_typing_bubble_list_row_text_view)
    TextView textView;

    public TypingBubbleListRow(Context context) {
        this(context, null);
    }

    public TypingBubbleListRow(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TypingBubbleListRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.bind(this);
        ((GradientDrawable) textView.getBackground()).setColor(getContext().getResources().getColor(R.color.rivet_light_gray));
    }

    public void resetColor() {
        ((GradientDrawable) textView.getBackground()).setColor(getContext().getResources().getColor(R.color.rivet_light_gray));
    }
}
