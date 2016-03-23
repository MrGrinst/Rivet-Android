package com.jumpintorivet.rivet.components.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RectMaskableImageView extends ImageView {
    private Rect mask;
    private Rect translatedMask;

    public RectMaskableImageView(Context context) {
        this(context, null);
    }

    public RectMaskableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RectMaskableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
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
}
