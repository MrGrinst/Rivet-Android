package com.jumpintorivet.rivet.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.utils.ColorUtil;
import com.jumpintorivet.rivet.utils.P;

import javax.inject.Inject;

public class SquareWithCenterText extends RelativeLayout {
    @Inject
    ColorUtil colorUtil;
    private TextView centerTextView;
    private int colorId;
    private RectF rect;

    public SquareWithCenterText(Context context) {
        this(context, null);
    }

    public SquareWithCenterText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SquareWithCenterText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((MyApplication) context.getApplicationContext()).inject(this);
        setWillNotDraw(false);
        colorId = R.color.rivet_orange;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        centerTextView = new TextView(getContext());
        RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_HORIZONTAL);
        params.addRule(CENTER_VERTICAL);
        params.topMargin = 0;
        params.bottomMargin = 0;
        centerTextView.setText("0");
        centerTextView.setLayoutParams(params);
        centerTextView.setTextColor(getResources().getColor(colorId));
        centerTextView.setId(R.id.relativeLayout1);
        centerTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.large_explanatory_text_size));
        addView(centerTextView);
    }

    public void setColor(int colorId) {
        this.colorId = colorId;
        centerTextView.setTextColor(getResources().getColor(colorId));
        invalidate();
    }

    public void setText(String topText, String bottomText) {
        if (bottomText != null) {
            View spacer = new View(getContext());
            RelativeLayout.LayoutParams spacerParams = new LayoutParams(getWidth(), P.convert(45));
            spacerParams.addRule(ALIGN_PARENT_TOP);
            spacerParams.topMargin = 0;
            spacerParams.bottomMargin = 0;
            spacer.setId(R.id.relativeLayout2);
            spacer.setLayoutParams(spacerParams);
            addView(spacer);
            TextView bottomTextView = new TextView(getContext());
            RelativeLayout.LayoutParams centerTextViewParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            centerTextViewParams.addRule(CENTER_HORIZONTAL);
            centerTextViewParams.addRule(BELOW, spacer.getId());
            centerTextViewParams.topMargin = 0;
            centerTextViewParams.bottomMargin = 0;
            centerTextView.setLayoutParams(centerTextViewParams);
            centerTextView.setPadding(0, 0, 0, 0);
            RelativeLayout.LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(BELOW, centerTextView.getId());
            params.addRule(CENTER_HORIZONTAL);
            params.topMargin = -P.convert(6);
            params.bottomMargin = 0;
            bottomTextView.setText(bottomText);
            bottomTextView.setLayoutParams(params);
            bottomTextView.setTextColor(getResources().getColor(colorId));
            bottomTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.small_explanatory_text_size));
            bottomTextView.setPadding(0, 0, 0, 0);
            addView(bottomTextView);
        }
        centerTextView.setText(topText);
    }

    public void setTextSize(int unit, float size, boolean bold) {
        centerTextView.setTextSize(unit, size);
        if (bold) {
            centerTextView.setTypeface(Typeface.DEFAULT_BOLD);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        drawSquareBorder(canvas);
        drawInnerSquare(canvas);
    }

    private void drawSquareBorder(Canvas canvas) {
        Paint paint = colorUtil.getColorUsingResourceId(colorId);
        paint.setAntiAlias(true);
        drawSquare(canvas, 0, 0, getWidth(), paint);
    }

    private void drawInnerSquare(Canvas canvas) {
        Paint paint = colorUtil.getColorUsingResourceId(android.R.color.white);
        paint.setAntiAlias(true);
        drawSquare(canvas, P.convert(10), P.convert(10), getWidth() - P.convert(20), paint);
    }

    private void drawSquare(Canvas canvas, float xOffset, float yOffset, float sideSize, Paint color) {
        rect = new RectF(xOffset, yOffset, xOffset + sideSize, yOffset + sideSize);
        canvas.drawRoundRect(rect, P.convert(7), P.convert(7), color);
    }
}
