package com.example.real.tool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationManagerCompat;

import com.example.real.AuctionBidActivity;
import com.example.real.Callback;
import com.example.real.R;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final String TAG = "FCM";
    FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(this, "UserProfile", null);

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {

        Log.d("FCM_Service", "Done?");
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();
        final String CHANNEL_ID = "HEADS_UP_NOTIFICATION";
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Heads Up Notification",
                NotificationManager.IMPORTANCE_HIGH
        );
        getSystemService(NotificationManager.class).createNotificationChannel(channel);
        Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(R.drawable.push_img)
                .setAutoCancel(true);
        NotificationManagerCompat.from(this).notify(1, notification.build());
        super.onMessageReceived(remoteMessage);

        Map<String, String> payload = remoteMessage.getData();
        String flag = payload.get("wFCM");

        if(flag.equals("ExistMessage")) {

            //Map<String, String> payload = remoteMessage.getData();
            String[] messagePath = payload.get("path").split("/");
            String chatRoomPath = "/" + messagePath[1] + "/" + messagePath[2] + "/" + messagePath[3];
            String fromUid = payload.get("fromUid");
            String toUid = payload.get("toUid");
            String message = payload.get("message");
            String time = payload.get("time");
            String fromToken = payload.get("fromToken");
            String toToken = payload.get("toToken");
            String imageUri = payload.get("imageUri");

            firestoreManagerForUserProfile.read("UserProfile", fromUid, new Callback() {
                @Override
                public void OnCallback(Object object) {
                    UserProfile fromUser = (UserProfile) object;
                    ArrayList<String> ChattingRoomID = fromUser.getChattingRoomID();
                    if (ChattingRoomID.contains(chatRoomPath) == false) {
                        ChattingRoomID.add(chatRoomPath);
                        firestoreManagerForUserProfile.update("UserProfile", fromUid, "ChattingRoomID", ChattingRoomID, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                int LogMessage = (int) object;
                                if (LogMessage == 0) {
                                    Log.d(TAG, "ISSUCCESSFUL");
                                } else {
                                    Log.d(TAG, "ISFAILURE");
                                }
                            }
                        });
                    }
                }
            });

            firestoreManagerForUserProfile.read("UserProfile", toUid, new Callback() {
                @Override
                public void OnCallback(Object object) {
                    UserProfile fromUser = (UserProfile) object;
                    ArrayList<String> ChattingRoomID = fromUser.getChattingRoomID();
                    if (ChattingRoomID.contains(chatRoomPath) == false) {
                        ChattingRoomID.add(chatRoomPath);
                        firestoreManagerForUserProfile.update("UserProfile", toUid, "ChattingRoomID", ChattingRoomID, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                int LogMessage = (int) object;
                                if (LogMessage == 0) {
                                    Log.d(TAG, "ISSUCCESSFUL");
                                } else {
                                    Log.d(TAG, "ISFAILURE");
                                }
                            }
                        });
                    }
                }
            });
        } else if(flag.equals("ExistUpperBidUser")){
            Log.d("FCM_Service", "Done?");
            sendNotificationForExistUpperBidUser(payload.get("contentId"));
        } else{

        }
    }

    private void sendNotificationForExistUpperBidUser(String msg){
        Intent intent = new Intent(this, AuctionBidActivity.class);
        intent.putExtra("ContentId", msg);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1000, intent, PendingIntent.FLAG_ONE_SHOT);
    }
}
