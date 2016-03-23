package com.jumpintorivet.rivet.activities.bar_on_bottom.conversation_list;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.utils.ConstUtil;
import com.jumpintorivet.rivet.utils.P;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class EmptyFeaturedListCell extends LinearLayout {
    @Bind(R.id.bar_on_bottom_list_featured_conversation_empty_list_fragment_cell_card_view)
    RelativeLayout cardView;
    @Bind(R.id.bar_on_bottom_list_featured_conversation_empty_list_fragment_cell_card_view_text)
    TextView textView;
    @Inject
    ConstUtil constUtil;

    public EmptyFeaturedListCell(Context context) {
        super(context);
    }

    public EmptyFeaturedListCell(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public EmptyFeaturedListCell(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ((MyApplication) getContext().getApplicationContext()).inject(this);
        ButterKnife.bind(this);
        LayoutParams cardViewParams = (LinearLayout.LayoutParams) cardView.getLayoutParams();
        cardViewParams.topMargin = (int) (Math.max(constUtil.getScreenHeight() - P.convert(500), P.convert(20)) / 2.0);
        cardViewParams.bottomMargin = (int) (Math.max(constUtil.getScreenHeight() - P.convert(500), P.convert(20)) / 2.0);
        cardView.setLayoutParams(cardViewParams);
    }

    public void setText(int stringId) {
        textView.setText(stringId);
    }
}
