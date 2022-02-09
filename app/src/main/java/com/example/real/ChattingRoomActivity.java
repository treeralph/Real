package com.example.real;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
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
}