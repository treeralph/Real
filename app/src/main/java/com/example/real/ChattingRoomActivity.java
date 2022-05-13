package com.example.real;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.adapter.RecyclerViewAdapterForChattingRoom;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChattingRoomActivity extends AppCompatActivity {

    private String TAG = "ChattingRoomActivity";

    private RecyclerView recyclerView;
    private LinearLayout contentsBtn;
    private LinearLayout userBtn;
    private LinearLayout editBtn;

    private FirebaseUser user;
    private FirestoreManager firestoreManagerForUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chatting_room);
        setContentView(R.layout.activity_chatting_room_design);

        contentsBtn = findViewById(R.id.ChattingRoomActivityContentsButton);
        userBtn = findViewById(R.id.ChattingRoomActivityUserButton);
        editBtn = findViewById(R.id.ChattingRoomActivityEditButton);

        contentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        userBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChattingRoomActivity.this, UserhistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestoreManagerForUserProfile = new FirestoreManager(this,"UserProfile", user.getUid());

        recyclerView = (RecyclerView) findViewById(R.id.ChattingRoomActivityChattingRoomRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
            @Override
            public void OnCallback(Object object) {
                Log.w(TAG, "userProfile read");
                UserProfile userProfile = (UserProfile) object;
                ArrayList<String> chattingRoomPathList = userProfile.getChattingRoomID();
                RecyclerViewAdapterForChattingRoom adapter = new RecyclerViewAdapterForChattingRoom(chattingRoomPathList, ChattingRoomActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public void onBackPressed() {
        ActivityManager activityManager = (ActivityManager) getApplication().getSystemService( Activity.ACTIVITY_SERVICE );
        ActivityManager.RunningTaskInfo task = activityManager.getRunningTasks( 10 ).get(0);
        Log.d(TAG, task.toString());
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