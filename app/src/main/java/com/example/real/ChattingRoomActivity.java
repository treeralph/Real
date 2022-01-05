package com.example.real;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.adapter.RecyclerViewAdapterForChattingRoom;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChattingRoomActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private FirebaseUser user;
    private FirestoreManager firestoreManagerForUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting_room);

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestoreManagerForUserProfile = new FirestoreManager(this,"UserProfile", user.getUid());

        recyclerView = (RecyclerView) findViewById(R.id.ChattingRoomActivityRecyclerView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
            @Override
            public void OnCallback(Object object) {
                UserProfile userProfile = (UserProfile) object;
                ArrayList<String> chattingRoomPathList = userProfile.getChattingRoomID();

                RecyclerViewAdapterForChattingRoom adapter =
                        new RecyclerViewAdapterForChattingRoom(chattingRoomPathList, ChattingRoomActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });


    }
}