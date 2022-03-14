package com.example.real;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
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
    @Override
    public void onBackPressed() {
        ActivityManager activityManager = (ActivityManager) getApplication().getSystemService( Activity.ACTIVITY_SERVICE );
        ActivityManager.RunningTaskInfo task = activityManager.getRunningTasks( 10 ).get(0);
        Log.d("TOPTOPTOP", task.toString());
        if(task.numActivities == 1){

            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.exit_check_dialog);

            CardView yesBtn = dialog.findViewById(R.id.exitCheckDialogYesButton);
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            CardView noBtn = dialog.findViewById(R.id.exitCheckDialogNoButton);
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }else{
            finish();
        }
    }
}