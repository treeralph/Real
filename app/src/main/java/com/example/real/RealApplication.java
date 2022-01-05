package com.example.real;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.real.data.LoadingHandler;

public class RealApplication extends Application {

    public LoadingHandler loadinghandler;
    
    @Override
    public void onCreate() {
        super.onCreate();
        loadinghandler = new LoadingHandler(Looper.getMainLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message msg) {
                Toast.makeText(getApplicationContext(),(String) msg.obj,Toast.LENGTH_SHORT).show();
                return false;
            }
        });

    }


    
    
}
