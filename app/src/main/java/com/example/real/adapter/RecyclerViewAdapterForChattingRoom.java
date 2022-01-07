package com.example.real.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.Callback;
import com.example.real.ChattingActivity;
import com.example.real.R;
import com.example.real.data.Content;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class RecyclerViewAdapterForChattingRoom extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private ArrayList<String> chattingRoomPathList;
    private Context context;
    private FirebaseUser user;

    private FirestoreManager firestoreManagerForUserProfile;
    private FirestoreManager firestoreManagerForContent;
    private StorageManager storageManagerForContent;

    private String contentUID;

    public RecyclerViewAdapterForChattingRoom(ArrayList<String> chattingRoomPathList, Context context) {

        this.chattingRoomPathList = chattingRoomPathList;
        this.context = context;

        user = FirebaseAuth.getInstance().getCurrentUser();
        firestoreManagerForUserProfile = new FirestoreManager(context, "UserProfile", user.getUid());
        firestoreManagerForContent = new FirestoreManager(context, "Content", user.getUid());
        storageManagerForContent = new StorageManager(context, "image", user.getUid());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.chatting_room_item, parent, false);
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

        firestoreManagerForContent.read("Content", contentId, new Callback() {
            @Override
            public void OnCallback(Object object) {
                Content content = (Content) object;
                String contentTitle = content.getTitle();
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
                            }
                        });
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
        TextView chattingRoomItemContentTitleTextView;
        TextView chattingRoomItemClientUserNickName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            chattingRoomItemLinearLayout = itemView.findViewById(R.id.chattingRoomItemLinearLayout);
            chattingRoomItemContentImageView = itemView.findViewById(R.id.chattingRoomItemContentImageView);
            chattingRoomItemContentTitleTextView = itemView.findViewById(R.id.chattingRoomItemContentTitleTextView);
            chattingRoomItemClientUserNickName = itemView.findViewById(R.id.chattingRoomItemClientUserNickName);
        }
    }
}
