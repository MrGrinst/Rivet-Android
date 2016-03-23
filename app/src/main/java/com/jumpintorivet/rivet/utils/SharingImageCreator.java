package com.jumpintorivet.rivet.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.NinePatchDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.injection.ForApplication;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class SharingImageCreator {
    private MyApplication application;

    @Inject
    public SharingImageCreator(@ForApplication MyApplication application) {
        this.application = application;
    }

    public Uri createImageFromDescriptionAndHeadline(String description, String headline, boolean isFeatured) {
        if (description != null) {
            if (isFeatured && headline != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap startImage = BitmapFactory.decodeResource(application.getResources(), R.drawable.sharing_image_featured, options);
                TextPaint textPaint = new TextPaint();
                textPaint.setColor(application.getResources().getColor(R.color.rivet_light_blue));
                textPaint.setTextSize(30);
                textPaint.setAntiAlias(true);
                int totalHeightNeededForImage = 503;
                int imageWidth = 1024;
                int topOfDrawableArea = 212;
                int totalHeightOfDrawableArea = 228;
                int maxTextWidth = 740;
                int maxHeadlineWidth = 520;
                int totalHeightOfDrawnContent = 0;
                StaticLayout descriptionMeasured = new StaticLayout(description, textPaint, maxTextWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                Rect descriptionSize = new Rect(0, 0, maxTextWidth, descriptionMeasured.getHeight());
                totalHeightNeededForImage += descriptionSize.height();
                totalHeightOfDrawableArea += descriptionSize.height();
                totalHeightOfDrawnContent += descriptionSize.height();
                byte[] chunk = startImage.getNinePatchChunk();
                NinePatchDrawable ninepatch = new NinePatchDrawable(application.getResources(), startImage, chunk, new Rect(), "sharing_image");
                Bitmap output = Bitmap.createBitmap(imageWidth, totalHeightNeededForImage, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);
                ninepatch.setBounds(0, 0, imageWidth, totalHeightNeededForImage);
                ninepatch.draw(canvas);
                textPaint.setUnderlineText(true);
                textPaint.setTypeface(Typeface.DEFAULT_BOLD);
                textPaint.setTextSize(32);
                StaticLayout headlineMeasured = new StaticLayout(headline, textPaint, maxHeadlineWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                Rect headlineSize = new Rect(0, 0, maxHeadlineWidth, headlineMeasured.getHeight());
                StaticLayout drawHeadline = new StaticLayout(headline, textPaint, maxHeadlineWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                canvas.translate((float) ((imageWidth - maxHeadlineWidth) / 2.0), (float) (((topOfDrawableArea + (totalHeightOfDrawableArea - totalHeightOfDrawnContent) / 2.0) - topOfDrawableArea - headlineSize.height()) / 2.0 + topOfDrawableArea + 4));
                drawHeadline.draw(canvas);
                canvas.translate(-(float) ((imageWidth - maxHeadlineWidth) / 2.0), -(float) (((topOfDrawableArea + (totalHeightOfDrawableArea - totalHeightOfDrawnContent) / 2.0) - topOfDrawableArea - headlineSize.height()) / 2.0 + topOfDrawableArea));
                textPaint.setColor(application.getResources().getColor(R.color.rivet_off_black));
                textPaint.setUnderlineText(false);
                textPaint.setTypeface(Typeface.DEFAULT);
                textPaint.setTextSize(30);
                StaticLayout drawDescription = new StaticLayout(description, textPaint, maxTextWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                canvas.translate((float) ((imageWidth - maxTextWidth) / 2.0), (float) (topOfDrawableArea + (totalHeightOfDrawableArea - totalHeightOfDrawnContent) / 2.0));
                drawDescription.draw(canvas);
                canvas.translate(-(float) ((imageWidth - maxTextWidth) / 2.0), -(float) (topOfDrawableArea + (totalHeightOfDrawableArea - totalHeightOfDrawnContent) / 2.0));
                startImage.recycle();
                System.gc();
                String path = MediaStore.Images.Media.insertImage(application.getContentResolver(), output, "Title", null);
                output.recycle();
                System.gc();
                return Uri.parse(path);
            } else {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inScaled = false;
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap startImage = BitmapFactory.decodeResource(application.getResources(), R.drawable.sharing_image, options);
                TextPaint textPaint = new TextPaint();
                textPaint.setColor(application.getResources().getColor(R.color.rivet_dark_blue));
                textPaint.setTextSize(30);
                textPaint.setAntiAlias(true);
                int totalHeightNeededForImage = 503;
                int imageWidth = 1024;
                int topOfDrawableArea = 212;
                int totalHeightOfDrawableArea = 228;
                int maxTextWidth = 740;
                int totalHeightOfDrawnContent = 0;
                StaticLayout descriptionMeasured = new StaticLayout(description, textPaint, maxTextWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                Rect descriptionSize = new Rect(0, 0, maxTextWidth, descriptionMeasured.getHeight());
                totalHeightNeededForImage += descriptionSize.height();
                totalHeightOfDrawableArea += descriptionSize.height();
                totalHeightOfDrawnContent += descriptionSize.height();
                byte[] chunk = startImage.getNinePatchChunk();
                NinePatchDrawable ninepatch = new NinePatchDrawable(application.getResources(), startImage, chunk, new Rect(), "sharing_image");
                Bitmap output = Bitmap.createBitmap(imageWidth, totalHeightNeededForImage, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(output);
                ninepatch.setBounds(0, 0, imageWidth, totalHeightNeededForImage);
                ninepatch.draw(canvas);
                StaticLayout drawDescription = new StaticLayout(description, textPaint, maxTextWidth, Layout.Alignment.ALIGN_CENTER, 1.0f, 0.0f, false);
                canvas.translate((float) ((imageWidth - maxTextWidth) / 2.0), (float) (topOfDrawableArea + (totalHeightOfDrawableArea - totalHeightOfDrawnContent) / 2.0));
                drawDescription.draw(canvas);
                canvas.translate(-(float) ((imageWidth - maxTextWidth) / 2.0), -(float) (topOfDrawableArea + (totalHeightOfDrawableArea - totalHeightOfDrawnContent) / 2.0));
                startImage.recycle();
                System.gc();
                String path = MediaStore.Images.Media.insertImage(application.getContentResolver(), output, "Title", null);
                output.recycle();
                System.gc();
                return Uri.parse(path);
            }
        }
        return null;
    }
}
