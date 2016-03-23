package com.jumpintorivet.rivet.components.base;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Path;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.jumpintorivet.rivet.utils.P;

public class RoundedImageView extends com.makeramen.roundedimageview.RoundedImageView {
    private static float radius = P.convert(7);

    public RoundedImageView(Context context) {
        this(context, null);
    }

    public RoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCornerRadius(radius, radius, 0, 0);
    }
}
