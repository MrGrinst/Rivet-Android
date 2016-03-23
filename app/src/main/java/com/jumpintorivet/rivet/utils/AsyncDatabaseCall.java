package com.jumpintorivet.rivet.utils;

import android.os.AsyncTask;

import com.orm.SugarRecord;

import java.util.List;

import rx.functions.Action1;

public class AsyncDatabaseCall<T extends SugarRecord> extends AsyncTask<Void, Void, Void> {
    private Class<T> tClass;
    private String where;
    private String[] whereArgs;
    private Action1<List<T>> callback;

    public AsyncDatabaseCall(Class<T> tClass, String where, String[] whereArgs, Action1<List<T>> callback) {
        this.tClass = tClass;
        this.where = where;
        this.whereArgs = whereArgs;
        this.callback = callback;
    }

    @Override
    protected Void doInBackground(Void... params) {
        List<T> records = SugarRecord.find(tClass, where, whereArgs);
        callback.call(records);
        return null;
    }
}
