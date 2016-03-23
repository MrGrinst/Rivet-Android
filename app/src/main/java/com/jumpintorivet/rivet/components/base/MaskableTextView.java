package com.jumpintorivet.rivet.components.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.text.Layout;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.widget.TextView;

public class MaskableTextView extends TextView {
    private Rect mask;
    private Rect translatedMask;

    public MaskableTextView(Context context) {
        this(context, null);
    }

    public MaskableTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MaskableTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setMask(Rect mask) {
        this.mask = mask;
        translatedMask = new Rect();
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mask != null) {
            translatedMask.set(mask);
            translatedMask.offset((int) -getX(), (int) -getY());
            canvas.clipRect(translatedMask);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout layout = getLayout();
        if (layout != null) {
            int width = (int) FloatMath.ceil(getMaxLineWidth(layout))
                    + getCompoundPaddingLeft() + getCompoundPaddingRight();
            int height = getMeasuredHeight();
            setMeasuredDimension(width, height);
        }
    }

    private float getMaxLineWidth(Layout layout) {
        float max_width = 0.0f;
        int lines = layout.getLineCount();
        for (int i = 0; i < lines; i++) {
            if (layout.getLineWidth(i) > max_width) {
                max_width = layout.getLineWidth(i);
            }
        }
        return max_width;
    }
}
