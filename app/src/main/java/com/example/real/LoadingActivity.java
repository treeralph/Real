package com.example.real;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class LoadingActivity extends Activity {

    // 박강혁 바보 멍청이
    // 족까
    public static Context LoadingContext;
    LoadingHandler handler;
    LottieAnimationView lottie;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        LoadingContext = this;
        handler = new LoadingHandler();
    }
    class LoadingHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            String tempText = (String)msg.obj;
            Toast.makeText(getApplicationContext(), tempText, Toast.LENGTH_SHORT).show();
            finish();
            overridePendingTransition(R.anim.fadeout, R.anim.fadein);
            Intent intent = new Intent(LoadingActivity.this, ContentsActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        }
    }
}