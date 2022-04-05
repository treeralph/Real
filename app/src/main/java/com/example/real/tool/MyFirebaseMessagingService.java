package com.example.real.tool;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.real.AuctionBidActivity;
import com.example.real.AuctionContentActivity;
import com.example.real.Callback;
import com.example.real.ChattingActivity;
import com.example.real.ContentActivity;
import com.example.real.PeekUserProfileActivity;
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
        super.onMessageReceived(remoteMessage);

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

        Map<String, String> payload = remoteMessage.getData();
        String flag = payload.get("wFCM");

        if(flag.equals("ExistMessage")) {

            Notification.Builder notification = new Notification.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.push_img)
                    .setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(1, notification.build());

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

            Intent intent = new Intent(this, AuctionContentActivity.class);
            intent.putExtra("contentId", payload.get("contentId"));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText(body)
                    .setSmallIcon(R.drawable.push_img)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true);
            NotificationManagerCompat.from(this).notify(1, notification.build());

            Log.d("FCM_Service", "Done?123");
            //sendNotificationForExistUpperBidUser(payload.get("contentId"));

        } else if(flag.equals("ScheduledAlarm")){

            /**
             * description format
             *      { 알람종류 },{ Title },{ body },{ Intent Extra (regax : "/") }, ...
             * */
            String description = payload.get("description");
            String time = payload.get("time");
            String[] descriptionSplit = description.split(",");
            String dFlag = descriptionSplit[0];

            if(dFlag.equals("GonnaDeal")){

                Log.d("FCM_Service", "ScheduledAlarm/GonnaDeal");

                String tempTitle = descriptionSplit[1];
                String tempBody = descriptionSplit[2];
                String[] intentExtraList = descriptionSplit[3].split("/");

                String chattingRoomPath = intentExtraList[0];
                String contentId = intentExtraList[1];
                String clientUid = intentExtraList[2];
                String contentUID= intentExtraList[3];

                Intent intent = new Intent(this, ChattingActivity.class);
                intent.putExtra("databasePath", chattingRoomPath);
                intent.putExtra("contentId", contentId);
                intent.putExtra("userUID", clientUid);
                intent.putExtra("contentUID", contentUID);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(intent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(tempTitle)
                        .setContentText(tempBody)
                        .setSmallIcon(R.drawable.push_img)
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true);
                NotificationManagerCompat.from(this).notify(1, notification.build());

            }else if(dFlag.equals("ExperienceDeal")){

                Log.d("FCM_Service", "ScheduledAlarm/ExperienceDeal");

                String tempTitle = descriptionSplit[1];
                String tempBody = descriptionSplit[2];
                String[] intentExtraList = descriptionSplit[3].split("/");

                String contentUID= intentExtraList[0];

                Intent intent = new Intent(this, PeekUserProfileActivity.class);
                intent.putExtra("userProfileUID", contentUID);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(intent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(tempTitle)
                        .setContentText(tempBody)
                        .setSmallIcon(R.drawable.push_img)
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true);
                NotificationManagerCompat.from(this).notify(1, notification.build());

            }else if(dFlag.equals("ChickenBid")){

                Log.d("FCM_Service", "ScheduledAlarm/ChickenBid");

                String tempTitle = descriptionSplit[1];
                String tempBody = descriptionSplit[2];
                String[] intentExtraList = descriptionSplit[3].split("/");

                String chattingRoomPath = intentExtraList[0];
                String contentId = intentExtraList[1];
                String clientUid = intentExtraList[2];
                String contentUID= intentExtraList[3];

                Intent intent = new Intent(this, ChattingActivity.class);
                intent.putExtra("databasePath", chattingRoomPath);
                intent.putExtra("contentId", contentId);
                intent.putExtra("userUID", clientUid);
                intent.putExtra("contentUID", contentUID);

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
                stackBuilder.addNextIntentWithParentStack(intent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                        .setContentTitle(tempTitle)
                        .setContentText(tempBody)
                        .setSmallIcon(R.drawable.push_img)
                        .setContentIntent(resultPendingIntent)
                        .setAutoCancel(true);
                NotificationManagerCompat.from(this).notify(1, notification.build());

            }else if(dFlag.equals("Auctioneer")){

            }else{

            }


        } else{

        }
    }

    private void sendNotificationForExistUpperBidUser(String msg){
        Intent intent = new Intent(this, AuctionBidActivity.class);
        intent.putExtra("ContentId", msg);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 1000, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
}
