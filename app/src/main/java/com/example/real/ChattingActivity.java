package com.example.real;

import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.adapter.RecyclerViewAdapterForMessages;
import com.example.real.data.Message;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.RealTimeDatabaseManager;
import com.example.real.databasemanager.StorageManager;
import com.google.android.gms.common.GooglePlayServicesIncorrectManifestValueException;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.type.DateTime;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

public class ChattingActivity extends AppCompatActivity {

    ScrollView scrollView;
    RecyclerView messageRecyclerView;
    EditText messageEditText;
    ImageView messageSendBtn;
    ImageView additionalFunctionsBtn;
    ImageView finishBtn;
    TextView moveContentBtn;
    ImageView scheduleBtn;
    View emptyview;

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
    StorageManager storageManagerForMessageImage;

    RecyclerViewAdapterForMessages adapter;

    Button chooseImageButton;
    Button sendImageButton;
    ImageView imagePreviewImageView;
    Bitmap img;
    String imageUri;

    Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_chatting);
        setContentView(R.layout.activity_chatting_design);

        user = FirebaseAuth.getInstance().getCurrentUser();

        scrollView = findViewById(R.id.ChattingActivityScrollView);

        messageRecyclerView = findViewById(R.id.ChattingActivityMessageRecyclerViewDesign);
        messageRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        messageRecyclerView.setLayoutManager(layoutManager);
        emptyview = findViewById(R.id.ChattingActivityEmptyView);

        messageEditText = findViewById(R.id.ChattingActivityMessageEditTextDesign);
        messageSendBtn = findViewById(R.id.ChattingActivitySendButtonDesign);
        additionalFunctionsBtn = findViewById(R.id.ChattingActivityAdditionalFunctionsButtonDesign);
        finishBtn = findViewById(R.id.ChattingActivityFinishButtonDesign);
        moveContentBtn = findViewById(R.id.ChattingActivityMoveContentButtonDesign);
        scheduleBtn = findViewById(R.id.ChattingActivityScheduleButtonDesign);

        databasePath = getIntent().getStringExtra("databasePath"); // databasePath
        contentId = getIntent().getStringExtra("contentId");
        userUID = getIntent().getStringExtra("userUID"); // userUID is not current user. It's part of path.
        contentUID = getIntent().getStringExtra("contentUID");

        realTimeDatabaseManager = new RealTimeDatabaseManager(this, "Messages", user.getUid());
        firestoreManagerForUserProfile = new FirestoreManager(this, "UserProfile", user.getUid());
        storageManagerForUserProfile = new StorageManager(this, "UserProfileImage", user.getUid());

        finishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        scheduleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChattingActivity.this, ScheduleActivity.class);


                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(emptyview, "testView");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ChattingActivity.this, pairs);
                startActivityForResult(intent,1,options.toBundle());
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        moveContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        additionalFunctionsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = new Dialog(ChattingActivity.this, android.R.style.Theme_Black_NoTitleBar);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
                dialog.setCancelable(true);

                LayoutInflater inflater = dialog.getLayoutInflater();
                View dialogView = inflater.inflate(R.layout.activity_chatting_additional_functions, null);

                chooseImageButton = dialogView.findViewById(R.id.ChattingActivityAdditionalFunctionsImageButton);
                sendImageButton = dialogView.findViewById(R.id.ChattingActivityAdditionalFunctionsSendButton);
                imagePreviewImageView = dialogView.findViewById(R.id.ChattingActivityAdditionalFunctionsImagePreviewImageView);

                Log.d("TESTTESTTEST", chooseImageButton.toString());

                chooseImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(intent, 0);
                    }
                });


                sendImageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

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

                        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
                        LocalDateTime now = LocalDateTime.now();
                        String nowString = now.format(formatter);

                        imageUri = "MessageImage/" + fromUserUid + "/" + nowString;
                        storageManagerForMessageImage = new StorageManager(ChattingActivity.this, "MessageImage", user.getUid());
                        storageManagerForMessageImage.upload(imageUri, img, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                Message message = new Message(fromUserUid, toUserUid, "", fromUserToken, toUserToken, imageUri);
                                realTimeDatabaseManager.writeMessage(databasePath, message);
                                messageEditText.setText("");
                            }
                        });
                        dialog.dismiss();
                    }

                });
                dialog.setContentView(dialogView);
                dialog.show();
            }
        });

        messageSendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String msg = messageEditText.getText().toString();
                if(!msg.isEmpty()) {
                    if (user.getUid().equals(userUID)) {
                        fromUserToken = userUIDToken;
                        fromUserUid = userUID;
                        toUserToken = contentUIDToken;
                        toUserUid = contentUID;
                    } else {
                        fromUserToken = contentUIDToken;
                        fromUserUid = contentUID;
                        toUserToken = userUIDToken;
                        toUserUid = userUID;
                    }

                    Message message = new Message(fromUserUid, toUserUid, msg, fromUserToken, toUserToken, "");
                    realTimeDatabaseManager.writeMessage(databasePath, message);
                    messageEditText.setText("");
                }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){
            if(resultCode == RESULT_OK){
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    img = BitmapFactory.decodeStream(in);
                    in.close();

                    imagePreviewImageView.setImageBitmap(img);
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
        if(requestCode == 1){
            String X = data.getStringExtra("TEXTEDSCHEDULE");
            messageEditText.setText(X);
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}