package com.jumpintorivet.rivet.components.conversation_view;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.jumpintorivet.rivet.R;
import com.jumpintorivet.rivet.application.MyApplication;
import com.jumpintorivet.rivet.utils.ColorUtil;
import com.jumpintorivet.rivet.utils.ConstUtil;
import com.jumpintorivet.rivet.utils.P;
import com.squareup.otto.Bus;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PrivacyMask extends View {
    private static final int privacyMaskWidth = 16;
    private static final int privacyMaskHeight = 50;
    private static final int privacyMaskHitBoxWidth = 20;

    @Inject
    ConstUtil constUtil;
    @Inject
    ColorUtil colorUtil;
    @Inject
    Bus bus;
    private List<MessageListRow> cellsWithMask;
    private Rect mask;
    private int lastMarginValue;
    private RectF drawingRect;
    private View backgroundMaskView;
    private boolean isOnRight;
    private boolean isAnimatingBounce;
    private ValueAnimator bounceValueAnimator;

    public PrivacyMask(Context context) {
        this(context, null);
    }

    public PrivacyMask(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PrivacyMask(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        ((MyApplication) context.getApplicationContext()).inject(this);
        cellsWithMask = new ArrayList<>();
        mask = new Rect(constUtil.getScreenWidth(), 0, constUtil.getScreenWidth() * 2, constUtil.getScreenHeight());
        isOnRight = true;
        drawingRect = new RectF();
        backgroundMaskView = new View(getContext());
        backgroundMaskView.setBackgroundColor(getResources().getColor(R.color.rivet_privacy_mask_background_color));
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(constUtil.getScreenWidth(), constUtil.getScreenHeight());
        params.leftMargin = constUtil.getScreenWidth();
        params.topMargin = 0;
        backgroundMaskView.setLayoutParams(params);
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) v.getLayoutParams();
                double centerValue = (constUtil.getScreenWidth() - P.convert(privacyMaskHitBoxWidth)) / 2.0;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_MOVE:
                        int newLeftMarginValue = (int) event.getRawX() - (getWidth() / 2);
                        if (newLeftMarginValue + P.convert(privacyMaskHitBoxWidth) <= constUtil.getScreenWidth()
                                && newLeftMarginValue >= 0) {
                            setMaskX(newLeftMarginValue);
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        lastMarginValue = params.leftMargin;
                        if (params.leftMargin < centerValue) {
                            slideLeft(true);
                        } else {
                            slideRight(true);
                        }
                        break;

                    case MotionEvent.ACTION_DOWN:
                        if (bounceValueAnimator != null && bounceValueAnimator.isStarted()) {
                            stopBounceAnimation();
                        }
                        v.setLayoutParams(params);
                        lastMarginValue = params.leftMargin;
                        break;
                }
                return true;
            }
        });
    }

    public static FrameLayout.LayoutParams getDefaultLayoutParams(Resources resources, ConstUtil constUtil, int toolbarHeight) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(P.convert(privacyMaskHitBoxWidth), constUtil.getScreenHeight() - toolbarHeight);
        params.topMargin = toolbarHeight;
        params.leftMargin = constUtil.getScreenWidth() - P.convert(privacyMaskHitBoxWidth);
        return params;
    }

    public static FrameLayout.LayoutParams getConversationViewingLayoutParams(Resources resources, ConstUtil constUtil, int topMargin) {
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(P.convert(privacyMaskHitBoxWidth), constUtil.getScreenHeight() - topMargin);
        params.topMargin = topMargin;
        params.leftMargin = constUtil.getScreenWidth() - P.convert(privacyMaskHitBoxWidth);
        return params;
    }

    public void setIsOnRight(boolean isOnRight) {
        if (getLayoutParams() != null) {
            lastMarginValue = ((FrameLayout.LayoutParams) getLayoutParams()).leftMargin;
        }
        if (isOnRight) {
            slideRight(false);
        } else {
            slideLeft(false);
        }
    }

    public boolean isOnRight() {
        return isOnRight;
    }

    private void slideRight(boolean animate) {
        if (animate) {
            ObjectAnimator oa = ObjectAnimator.ofFloat(this, "maskX", getMaskX(), constUtil.getScreenWidth() - P.convert(privacyMaskHitBoxWidth));
            oa.setDuration(300);
            oa.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    bus.post(new PrivacyMaskSlidRightEvent());
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            oa.start();
        } else {
            setMaskX(constUtil.getScreenWidth() - P.convert(privacyMaskHitBoxWidth));
        }
        isOnRight = true;
    }

    private void slideLeft(boolean animate) {
        if (animate) {
            ObjectAnimator oa = ObjectAnimator.ofFloat(this, "maskX", getMaskX(), 0);
            oa.setDuration(300);
            oa.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    bus.post(new PrivacyMaskSlidLeftEvent());
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            oa.start();
        } else {
            setMaskX(0);
        }
        isOnRight = false;
    }

    public View getBackgroundMaskView() {
        return backgroundMaskView;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int leftMargin = ((FrameLayout.LayoutParams) getLayoutParams()).leftMargin;
        Paint paint = colorUtil.getColorUsingResourceId(R.color.rivet_off_black);
        paint.setAntiAlias(true);
        if (leftMargin < (constUtil.getScreenWidth() - P.convert(privacyMaskHitBoxWidth)) / 2.0) {
            drawingRect.set(0, 0, P.convert(privacyMaskWidth), P.convert(privacyMaskHeight));
            canvas.drawRect(0, 0, P.convert(7), P.convert(privacyMaskHeight), paint);
            canvas.drawRect(0, 0, P.convert(privacyMaskWidth), P.convert(7), paint);
            canvas.drawRoundRect(drawingRect, P.convert(5), P.convert(5), paint);
        } else {
            drawingRect.set(P.convert(privacyMaskHitBoxWidth - privacyMaskWidth), 0, P.convert(privacyMaskHitBoxWidth), P.convert(privacyMaskHeight));
            canvas.drawRect(P.convert(privacyMaskHitBoxWidth - 7), 0, P.convert(privacyMaskHitBoxWidth), P.convert(privacyMaskHeight), paint);
            canvas.drawRect(P.convert(privacyMaskHitBoxWidth - privacyMaskWidth), 0, P.convert(privacyMaskHitBoxWidth), P.convert(7), paint);
            canvas.drawRoundRect(drawingRect, P.convert(5), P.convert(5), paint);
        }
    }

    public void addMaskToCell(MessageListRow cmlr) {
        cmlr.setPrivacyMask(mask);
        cellsWithMask.add(cmlr);
    }

    public void translateMaskX(int xTranslate) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) backgroundMaskView.getLayoutParams();
        params.leftMargin += xTranslate;
        backgroundMaskView.setLayoutParams(params);
        mask.offset(xTranslate, 0);
        for (MessageListRow cmlr : cellsWithMask) {
            cmlr.updatePrivacyMask();
        }
    }

    public void startBounceAnimation() {
        isAnimatingBounce = true;
        bounceValueAnimator = ValueAnimator.ofFloat(0, 0.25f, 0.46f, 0.64f, 0.78f, 0.89f, 0.97f, 1, 1, 0.97f, 0.89f, 0.78f, 0.64f, 0.46f, 0.25f, 0, 0.18f, 0.32f, 0.42f, 0.48f, 0.5f, 0.48f, 0.42f, 0.32f, 0.18f, 0, 0.14f, 0.21f, 0.25f, 0.21f, 0.14f, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
        bounceValueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                FrameLayout.LayoutParams params = ((FrameLayout.LayoutParams) getLayoutParams());
                params.leftMargin = (int) (constUtil.getScreenWidth() - P.convert(privacyMaskHitBoxWidth) - (float) animation.getAnimatedValue() * P.convert(10));
                setLayoutParams(params);
            }
        });
        bounceValueAnimator.setDuration(2000);
        bounceValueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        bounceValueAnimator.start();
    }

    public void stopBounceAnimation() {
        isAnimatingBounce = false;
        bounceValueAnimator.cancel();
        FrameLayout.LayoutParams params = ((FrameLayout.LayoutParams) getLayoutParams());
        params.leftMargin = constUtil.getScreenWidth() - P.convert(privacyMaskHitBoxWidth);
        setLayoutParams(params);
    }

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
    }

    public float getMaskX() {
        return ((FrameLayout.LayoutParams) getLayoutParams()).leftMargin;
    }

    public void setMaskX(float xPosition) {
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) getLayoutParams();
        if (params != null) {
            double centerValue = (constUtil.getScreenWidth() - P.convert(privacyMaskHitBoxWidth)) / 2.0;
            params.leftMargin = (int) xPosition;
            if (lastMarginValue <= centerValue && params.leftMargin > centerValue) {
                translateMaskX(params.leftMargin - lastMarginValue + P.convert(privacyMaskHitBoxWidth));
            } else if (lastMarginValue > centerValue && params.leftMargin <= centerValue) {
                translateMaskX(params.leftMargin - lastMarginValue - P.convert(privacyMaskHitBoxWidth));
            } else {
                translateMaskX(params.leftMargin - lastMarginValue);
            }
            lastMarginValue = ((FrameLayout.LayoutParams) getLayoutParams()).leftMargin;
            setLayoutParams(params);
        }
    }

    public boolean isAnimatingBounce() {
        return isAnimatingBounce;
    }

    public static class PrivacyMaskSlidLeftEvent {
    }

    public static class PrivacyMaskSlidRightEvent {
    }
}
