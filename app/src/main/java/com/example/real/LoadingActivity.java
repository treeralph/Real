package com.example.real;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

public class LoadingActivity extends Activity {


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

            if(tempText.equals("ContentMakingDone")){
                Toast.makeText(getApplicationContext(), tempText, Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fadeout, R.anim.fadein);
                Intent intent = new Intent(LoadingActivity.this, ContentsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
            else if (tempText.equals("AuctionContentMakingDone")){
                Toast.makeText(getApplicationContext(), tempText, Toast.LENGTH_SHORT).show();
                finish();
                overridePendingTransition(R.anim.fadeout, R.anim.fadein);
                Intent intent = new Intent(LoadingActivity.this, ContentsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }


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