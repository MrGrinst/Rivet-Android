package com.jumpintorivet.rivet.components.start_new_conversation_box;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.utils.ColorUtil;
import com.jumpintorivet.rivet.utils.P;

import javax.inject.Inject;

public class RoundedSquareView extends RelativeLayout {
    @Inject
    ColorUtil colorUtil;
    private boolean isSearching;

    public RoundedSquareView(Context context) {
        this(context, null);
    }

    public RoundedSquareView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedSquareView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((MyApplication) context.getApplicationContext()).inject(this);
        setWillNotDraw(false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isSearching) {
            drawSquareBorder(canvas);
            drawInnerSquare(canvas);
        } else {
            drawNormalSquare(canvas);
        }
    }

    private void drawSquareBorder(Canvas canvas) {
        Paint paint = colorUtil.getColorUsingResourceId(R.color.rivet_dark_blue);
        paint.setAntiAlias(true);
        drawSquare(canvas, 0, 0, getWidth(), paint);
    }

    private void drawInnerSquare(Canvas canvas) {
        Paint paint = colorUtil.getColorUsingResourceId(R.color.rivet_off_white);
        paint.setAntiAlias(true);
        drawSquare(canvas, P.convert(10), P.convert(10), getWidth() - P.convert(20), paint);
    }

    private void drawNormalSquare(Canvas canvas) {
        Paint paint = colorUtil.getColorUsingResourceId(R.color.rivet_dark_blue);
        paint.setAntiAlias(true);
        drawSquare(canvas, 0, 0, getWidth(), paint);
    }

    private void drawSquare(Canvas canvas, float xOffset, float yOffset, float sideSize, Paint color) {
        RectF rect = new RectF(xOffset, yOffset, xOffset + sideSize, yOffset + sideSize);
        canvas.drawRoundRect(rect, P.convert(7), P.convert(7), color);
    }

    public void setIsSearching(boolean isSearching) {
        this.isSearching = isSearching;
        invalidate();
    }
}
