package com.example.real.adapter;

import android.accessibilityservice.GestureDescription;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.AuctionContentActivity;
import com.example.real.Callback;
import com.example.real.ChattingActivity;
import com.example.real.R;
import com.example.real.data.Content;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.tool.TimeTextTool;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.sql.Time;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class RecyclerViewAdapterForChattingRoom extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    static final String TAG = "ChattingRoomActivityRecyclerViewAdapter";

    private ArrayList<String> chattingRoomPathList;
    private Context context;
    private FirebaseUser user;

    private FirestoreManager firestoreManagerForUserProfile;
    private FirestoreManager firestoreManagerForContent;
    private StorageManager storageManagerForContent;
    private StorageManager storageManagerForUserProfileImage;

    private String contentUID;
    private String user1;
    private String user2;

    public RecyclerViewAdapterForChattingRoom(ArrayList<String> chattingRoomPathList, Context context) {

        this.chattingRoomPathList = chattingRoomPathList;
        this.context = context;

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestoreManagerForUserProfile = new FirestoreManager(context, "UserProfile", user.getUid());
        firestoreManagerForContent = new FirestoreManager(context, "Content", user.getUid());
        storageManagerForContent = new StorageManager(context, "image", user.getUid());
        storageManagerForUserProfileImage = new StorageManager(context, "UserProfileImage", user.getUid());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_room_item_design, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;

        // chatting room path format - Messages/{ContentId}/{ClientUID}
        String chattingRoomPath = chattingRoomPathList.get(position);
        String[] pathSplit = chattingRoomPath.split("/");
        String contentId = pathSplit[2];
        String clientUid = pathSplit[3];
        user2 = clientUid;

        myViewHolder.chattingRoomItemLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChattingActivity.class);
                intent.putExtra("databasePath", chattingRoomPath);
                intent.putExtra("contentId", contentId);
                intent.putExtra("userUID", clientUid);
                intent.putExtra("contentUID", contentUID);
                context.startActivity(intent);
            }
        });


        myViewHolder.chattingRoomItemMoveContentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        myViewHolder.chattingRoomItemMoveUserProfileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        // todo: how to handle asynchronous in try/catch statement.
        firestoreManagerForContent.read("Content", contentId, new Callback() {
            @Override
            public void OnCallback(Object object) {
                Content content = (Content) object;
                String contentTitle = content.getTitle();
                String contentTime = content.getTime();
                user1 = contentUID;
                TimeTextTool timeTextTool = new TimeTextTool(contentTime);
                myViewHolder.chattingRoomItemContentMoreInfoTextView.setText(timeTextTool.Time2Text());
                contentUID = content.getUid();
                myViewHolder.chattingRoomItemContentTitleTextView.setText(contentTitle);
                storageManagerForContent.downloadImg2View("image/" + contentId + "/100", "0", myViewHolder.chattingRoomItemContentImageView, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        firestoreManagerForUserProfile.read("UserProfile", clientUid, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                UserProfile userProfile = (UserProfile) object;
                                String clientNickName = userProfile.getNickName();
                                myViewHolder.chattingRoomItemClientUserNickName.setText(clientNickName);
                                if (user.getUid().equals(user1)) {
                                    storageManagerForUserProfileImage.downloadImg2View("UserProfileImage", user2, myViewHolder.chattingRoomItemUserProfileImageView, new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {

                                        }
                                    });
                                } else {
                                    storageManagerForUserProfileImage.downloadImg2View("UserProfileImage", user1, myViewHolder.chattingRoomItemUserProfileImageView, new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {

                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        }, new Callback() {
            @Override
            public void OnCallback(Object object) {
                myViewHolder.chattingRoomItemContentTitleTextView.setText("Expired Content");
                firestoreManagerForUserProfile.read("UserProfile", clientUid, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        UserProfile userProfile = (UserProfile) object;
                        String clientNickName = userProfile.getNickName();
                        myViewHolder.chattingRoomItemClientUserNickName.setText(clientNickName);
                        if (user.getUid().equals(user1)) {
                            storageManagerForUserProfileImage.downloadImg2View("UserProfileImage", user2, myViewHolder.chattingRoomItemUserProfileImageView, new Callback() {
                                @Override
                                public void OnCallback(Object object) {

                                }
                            });
                        } else {
                            storageManagerForUserProfileImage.downloadImg2View("UserProfileImage", user1, myViewHolder.chattingRoomItemUserProfileImageView, new Callback() {
                                @Override
                                public void OnCallback(Object object) {

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return chattingRoomPathList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout chattingRoomItemLinearLayout;
        ImageView chattingRoomItemContentImageView;
        ImageView chattingRoomItemUserProfileImageView;
        TextView chattingRoomItemContentTitleTextView;
        TextView chattingRoomItemContentMoreInfoTextView;
        TextView chattingRoomItemClientUserNickName;
        ImageView chattingRoomItemMoveContentButton;
        ImageView chattingRoomItemMoveUserProfileButton;

        public MyViewHolder(@NonNull View itemView) {

            super(itemView);
            chattingRoomItemLinearLayout = itemView.findViewById(R.id.chattingRoomItemLinearLayoutDesign);
            chattingRoomItemContentImageView = itemView.findViewById(R.id.ChattingRoomItemContentImageViewDesign);
            chattingRoomItemContentTitleTextView = itemView.findViewById(R.id.ChattingRoomItemContentTitleTextViewDesign);
            chattingRoomItemUserProfileImageView = itemView.findViewById(R.id.ChattingRoomItemUserProfileImageViewDesign);
            chattingRoomItemClientUserNickName = itemView.findViewById(R.id.ChattingRoomItemUserNickNameTextViewDesign);
            chattingRoomItemContentMoreInfoTextView = itemView.findViewById(R.id.ChattingRoomItemMoreInfoTextViewDesign);
        }
    }
}
