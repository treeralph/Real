package com.example.real.data;

import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class LoadingHandler extends Handler {
    public LoadingHandler(@NonNull Looper looper, @Nullable Handler.Callback callback) {
        super(looper, callback);
    }

}
