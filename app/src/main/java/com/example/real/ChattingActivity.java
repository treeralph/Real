package com.example.real;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.adapter.RecyclerViewAdapterForMessages;
import com.example.real.data.Message;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.RealTimeDatabaseManager;
import com.example.real.databasemanager.StorageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class ChattingActivity extends AppCompatActivity {

    ScrollView scrollView;
    RecyclerView messageRecyclerView;
    EditText messageEditText;
    Button messageSendBtn;

    FirebaseUser user;

    String databasePath;
    String contentId;
    String userUID;
    String contentUID;

    String userUIDToken;
    String contentUIDToken;

    String fromUserUid;
    String toUserUid;
    String fromUserToken;
    String toUserToken;

    Bitmap contentUserProfileImageBitmap;
    Bitmap userProfileImageBitmap;
    String contentUserProfileNickName;
    String userProfileNickName;

    RealTimeDatabaseManager realTimeDatabaseManager;
    FirestoreManager firestoreManagerForUserProfile;
    StorageManager storageManagerForUserProfile;

    RecyclerViewAdapterForMessages adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatting);

        user = FirebaseAuth.getInstance().getCurrentUser();

        scrollView = findViewById(R.id.ChattingActivityScrollView);

        messageRecyclerView = findViewById(R.id.ChattingActivityMessageRecyclerView);
        messageRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(layoutManager);

        messageEditText = findViewById(R.id.ChattingActivityMessageEditText);
        messageSendBtn = findViewById(R.id.ChattingActivitySendButton);

        databasePath = getIntent().getStringExtra("databasePath"); // databasePath
        contentId = getIntent().getStringExtra("contentId");
        userUID = getIntent().getStringExtra("userUID"); // userUID is not current user. It's part of path.
        contentUID = getIntent().getStringExtra("contentUID");

        realTimeDatabaseManager = new RealTimeDatabaseManager(this, "Messages", user.getUid());
        firestoreManagerForUserProfile = new FirestoreManager(this, "UserProfile", user.getUid());
        storageManagerForUserProfile = new StorageManager(this, "UserProfileImage", user.getUid());

        messageSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String msg = messageEditText.getText().toString();

                if(user.getUid().equals(userUID)){
                    fromUserToken = userUIDToken;
                    fromUserUid = userUID;
                    toUserToken = contentUIDToken;
                    toUserUid = contentUID;
                }else{
                    fromUserToken = contentUIDToken;
                    fromUserUid = contentUID;
                    toUserToken = userUIDToken;
                    toUserUid = userUID;
                }

                Message message = new Message(fromUserUid, toUserUid, msg, fromUserToken, toUserToken);
                realTimeDatabaseManager.writeMessage(databasePath, message);
                messageEditText.setText("");
            }
        });

        realTimeDatabaseManager.readMessageLooper(databasePath, new Callback() {
            @Override
            public void OnCallback(Object object) {
                Message tempMessage = (Message)object;
                try {
                    adapter.addItem(tempMessage);
                    messageRecyclerView.setAdapter(adapter);
                    messageRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        firestoreManagerForUserProfile.read("UserProfile", userUID, new Callback() {
            @Override
            public void OnCallback(Object object) {
                UserProfile userProfile = (UserProfile) object;
                userProfileNickName = userProfile.getNickName();
                userUIDToken = userProfile.getDeviceToken();
                firestoreManagerForUserProfile.read("UserProfile", contentUID, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        UserProfile userProfile = (UserProfile) object;
                        contentUserProfileNickName = userProfile.getNickName();
                        contentUIDToken = userProfile.getDeviceToken();
                        storageManagerForUserProfile.download("UserProfileImage", userUID, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                userProfileImageBitmap = (Bitmap) object;
                                storageManagerForUserProfile.download("UserProfileImage", contentUID, new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        contentUserProfileImageBitmap = (Bitmap) object;
                                        realTimeDatabaseManager.readMessage(databasePath, new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {
                                                ArrayList<Message> messageList = (ArrayList<Message>) object;
                                                adapter = new RecyclerViewAdapterForMessages(
                                                        ChattingActivity.this,
                                                        contentUserProfileImageBitmap,
                                                        userProfileImageBitmap,
                                                        contentUserProfileNickName,
                                                        userProfileNickName,
                                                        userUID,
                                                        contentUID
                                                );
                                                for(Message message : messageList){
                                                    adapter.addItem(message);
                                                }
                                                messageRecyclerView.setAdapter(adapter);
                                                messageRecyclerView.scrollToPosition(adapter.getItemCount() - 1);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });
    }
}