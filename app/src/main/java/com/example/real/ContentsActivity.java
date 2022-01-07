package com.example.real;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.adapter.RecyclerViewAdapterForContents;
import com.example.real.data.Contents;
import com.example.real.databasemanager.FirestoreManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ContentsActivity extends AppCompatActivity {

    FirebaseUser user;
    ImageView chatRoomBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contents);

        user = FirebaseAuth.getInstance().getCurrentUser();
        chatRoomBtn = findViewById(R.id.ContentsActivityChatRoomBtn);

        chatRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentsActivity.this, ChattingRoomActivity.class);
                startActivity(intent);
            }
        });


        // Device Token Renewing
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FIREBASEMESSAGING", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d("FIREBASEMESSAGING", token);

                        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(ContentsActivity.this, "UserProfile", user.getUid());
                        firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "DeviceToken", token, new Callback() {
                            @Override
                            public void OnCallback(Object object) {

                            }
                        });
                    }
                });



        RecyclerView recyclerView = findViewById(R.id.minetestRecyclerView);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FirestoreManager manager = new FirestoreManager(ContentsActivity.this, "Contents", user.getUid());
        manager.read("Contents", new Callback() {
            @Override
            public void OnCallback(Object object) {
                ArrayList<Contents> contentsList = (ArrayList<Contents>)object;

                RecyclerViewAdapterForContents adapter = new RecyclerViewAdapterForContents(contentsList, ContentsActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });

        ImageView makeContentBtn = (ImageView) findViewById(R.id.ContentsMakeContentBtn);
        makeContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.contentspopup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.popup1){
                            Intent intent = new Intent(ContentsActivity.this, MakeContentActivity.class);
                            startActivity(intent);
                        } else if(item.getItemId() == R.id.popup2){
                            Intent intent = new Intent(ContentsActivity.this, MakeAuctionContentActivity.class);
                            startActivity(intent);
                        } else if(item.getItemId() == R.id.popup3){

                        } else{

                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        ImageView SettingPopupImage = (ImageView) findViewById(R.id.ContentsSettingPopupImg);
        SettingPopupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu2 = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.contentssettingpopup, popupMenu2.getMenu());
                popupMenu2.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.Settingpopup1){
                            Intent intent = new Intent(ContentsActivity.this, SetUserProfileActivity.class);
                            startActivity(intent);
                        }else if(item.getItemId() == R.id.Settingpopup2){
                            Intent intent = new Intent(ContentsActivity.this, UserhistoryActivity.class);
                            startActivity(intent);
                        }
                        return false;
                    }
                });
                popupMenu2.show();
            }
        });



    }
}