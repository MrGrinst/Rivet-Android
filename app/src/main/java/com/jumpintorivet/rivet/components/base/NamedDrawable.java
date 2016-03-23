package com.jumpintorivet.rivet.components.base;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import com.makeramen.roundedimageview.RoundedDrawable;

import java.io.Serializable;

public class NamedDrawable implements Serializable {
    private Drawable normalDrawable;
    private Drawable roundedDrawable;
    private String name;

    public NamedDrawable(Drawable roundedDrawable, Drawable normalDrawable, String name, Resources res) {
        if (roundedDrawable == null) {
            this.normalDrawable = normalDrawable;
            if (normalDrawable instanceof RoundedDrawable) {
                this.roundedDrawable = normalDrawable;
                Bitmap bitmap = ((RoundedDrawable)normalDrawable).getSourceBitmap();
                this.normalDrawable = new BitmapDrawable(res, bitmap);
            } else {
                Bitmap bitmap = ((BitmapDrawable)normalDrawable).getBitmap();
                bitmap = bitmap.copy(bitmap.getConfig(), true);
                this.roundedDrawable = new BitmapDrawable(res, bitmap);
            }
        } else if (normalDrawable == null) {
            this.roundedDrawable = roundedDrawable;
            if (roundedDrawable instanceof RoundedDrawable) {
                Bitmap bitmap = ((RoundedDrawable)roundedDrawable).getSourceBitmap();
                this.normalDrawable = new BitmapDrawable(res, bitmap);
            } else {
                Bitmap bitmap = ((BitmapDrawable)roundedDrawable).getBitmap();
                bitmap = bitmap.copy(bitmap.getConfig(), true);
                this.normalDrawable = new BitmapDrawable(res, bitmap);
            }
        }
        this.name = name;
    }

    public Drawable getRoundedDrawable() {
        return roundedDrawable;
    }

    public void setRoundedDrawable(Drawable roundedDrawable) {
        this.roundedDrawable = roundedDrawable;
    }

    public Drawable getNormalDrawable() {
        return normalDrawable;
    }

    public void setNormalDrawable(Drawable normalDrawable) {
        this.normalDrawable = normalDrawable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
