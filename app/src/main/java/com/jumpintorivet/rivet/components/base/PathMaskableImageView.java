package com.jumpintorivet.rivet.components.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Region;
import android.util.AttributeSet;
import android.widget.ImageView;

public class PathMaskableImageView extends ImageView {
    private Rect mask;
    private Rect translatedMask;

    public PathMaskableImageView(Context context) {
        this(context, null);
    }

    public PathMaskableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PathMaskableImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        translatedMask = new Rect();
    }

    public void setMask(Rect mask) {
        this.mask = mask;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mask != null) {
            int location[] = new int[2];
            getLocationInWindow(location);
            translatedMask.set(mask);
            translatedMask.offset(-location[0], -location[1]);
            canvas.clipRect(translatedMask, Region.Op.DIFFERENCE);
        }
        super.onDraw(canvas);
    }
}

