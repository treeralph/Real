package com.example.real;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

public class IntroActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        try{
            String FCM_intent_to_auctionContent = getIntent().getStringExtra("contentId");
            Log.d("WOWMACHINE", FCM_intent_to_auctionContent);

            Handler h = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                    intent.putExtra("FCM_contentId", FCM_intent_to_auctionContent);
                    startActivity(intent);
                    finish();
                }
            };

            h.sendEmptyMessageDelayed(0, 1000); // 1500 is time in miliseconds

        } catch(Exception e){
            Handler h = new Handler(){
                @Override
                public void handleMessage(Message msg) {
                    Intent intent = new Intent(IntroActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            };

            h.sendEmptyMessageDelayed(0, 1000); // 1500 is time in miliseconds
        }


    }
}