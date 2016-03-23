package com.jumpintorivet.rivet.repositories;

import retrofit.Response;

public abstract class RivetCallback<T> {
    public abstract void success(Response<T> response);

    public abstract void failure(Throwable throwable, Response<T> response);
}
