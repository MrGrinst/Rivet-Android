package com.jumpintorivet.rivet.components.base;

import android.content.Context;
import android.content.res.Resources;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.jumpintorivet.rivet.R;

public class TimeAgoTextView extends TextView {
    private long mReferenceTime;
    private String mText;
    private Handler mHandler = new Handler();
    private TimeAgoTextView.UpdateTimeRunnable mUpdateTimeTask;
    private boolean isUpdateTaskRunning = false;

    public TimeAgoTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.init(context, attrs);
    }

    public TimeAgoTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        try {
            this.mReferenceTime = Long.valueOf(this.mText).longValue();
        } catch (NumberFormatException var7) {
            this.mReferenceTime = -1L;
        }

        this.setReferenceTime(this.mReferenceTime);
    }

    public void setReferenceTime(long referenceTime) {
        this.mReferenceTime = referenceTime;
        this.stopTaskForPeriodicallyUpdatingRelativeTime();
        this.mUpdateTimeTask = new TimeAgoTextView.UpdateTimeRunnable(this.mReferenceTime);
        this.startTaskForPeriodicallyUpdatingRelativeTime();
        this.updateTextDisplay();
    }

    private void updateTextDisplay() {
        if (this.mReferenceTime != -1L) {
            this.setText(timeAgo(this.mReferenceTime));
        }
    }

    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        this.startTaskForPeriodicallyUpdatingRelativeTime();
    }

    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        this.stopTaskForPeriodicallyUpdatingRelativeTime();
    }

    protected void onVisibilityChanged(View changedView, int visibility) {
        super.onVisibilityChanged(changedView, visibility);
        if (visibility != GONE && visibility != INVISIBLE) {
            this.startTaskForPeriodicallyUpdatingRelativeTime();
        } else {
            this.stopTaskForPeriodicallyUpdatingRelativeTime();
        }

    }

    private void startTaskForPeriodicallyUpdatingRelativeTime() {
        this.mHandler.post(this.mUpdateTimeTask);
        this.isUpdateTaskRunning = true;
    }

    private void stopTaskForPeriodicallyUpdatingRelativeTime() {
        if (this.isUpdateTaskRunning) {
            this.mHandler.removeCallbacks(this.mUpdateTimeTask);
            this.isUpdateTaskRunning = false;
        }

    }

    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        TimeAgoTextView.SavedState ss = new TimeAgoTextView.SavedState(superState);
        ss.referenceTime = this.mReferenceTime;
        return ss;
    }

    public void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof TimeAgoTextView.SavedState)) {
            super.onRestoreInstanceState(state);
        } else {
            TimeAgoTextView.SavedState ss = (TimeAgoTextView.SavedState) state;
            this.mReferenceTime = ss.referenceTime;
            super.onRestoreInstanceState(ss.getSuperState());
        }
    }

    private String timeAgo(long millis) {
        long diff = System.currentTimeMillis() - millis;
        Resources r = getContext().getResources();

        double seconds = Math.abs(diff) / 1000;
        double minutes = seconds / 60;
        double hours = minutes / 60;
        double days = hours / 24;
        double weeks = days / 7;
        double months = days / 30;
        double years = days / 365;

        String words;

        if (seconds < 45) {
            words = r.getString(R.string.time_ago_seconds, (int) Math.round(seconds));
        } else if (seconds < 90) {
            words = r.getString(R.string.time_ago_minute, 1);
        } else if (minutes < 45) {
            words = r.getString(R.string.time_ago_minute, (int) Math.round(minutes));
        } else if (minutes < 90) {
            words = r.getString(R.string.time_ago_hour, 1);
        } else if (hours < 24) {
            words = r.getString(R.string.time_ago_hours, (int) Math.round(hours));
        } else if (hours < 42) {
            words = r.getString(R.string.time_ago_day, 1);
        } else if (days < 6) {
            words = r.getString(R.string.time_ago_days, (int) Math.round(days));
        } else if (days < 10) {
            words = r.getString(R.string.time_ago_week, 1);
        } else if (months < 6) {
            words = r.getString(R.string.time_ago_weeks, (int) Math.round(weeks));
        } else if (months < 11) {
            words = r.getString(R.string.time_ago_months, (int) Math.round(months));
        } else if (months < 18) {
            words = r.getString(R.string.time_ago_year, 1);
        } else {
            words = r.getString(R.string.time_ago_years, (int) Math.round(years));
        }
        return words.trim();
    }

    public static class SavedState extends BaseSavedState {
        public static final Creator<TimeAgoTextView.SavedState> CREATOR = new Creator() {
            public TimeAgoTextView.SavedState createFromParcel(Parcel in) {
                return new TimeAgoTextView.SavedState(in);
            }

            public TimeAgoTextView.SavedState[] newArray(int size) {
                return new TimeAgoTextView.SavedState[size];
            }
        };
        private long referenceTime;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            this.referenceTime = in.readLong();
        }

        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeLong(this.referenceTime);
        }
    }

    private class UpdateTimeRunnable implements Runnable {
        private long mRefTime;

        UpdateTimeRunnable(long refTime) {
            this.mRefTime = refTime;
        }

        public void run() {
            long difference = Math.abs(System.currentTimeMillis() - this.mRefTime);
            long interval = 60000L;
            if (difference > 604800000L) {
                interval = 604800000L;
            } else if (difference > 86400000L) {
                interval = 86400000L;
            } else if (difference > 3600000L) {
                interval = 3600000L;
            }

            TimeAgoTextView.this.updateTextDisplay();
            TimeAgoTextView.this.mHandler.postDelayed(this, interval);
        }
    }
}
