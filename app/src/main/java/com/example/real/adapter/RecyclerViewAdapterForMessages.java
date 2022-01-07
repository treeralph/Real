package com.example.real.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.Callback;
import com.example.real.R;
import com.example.real.data.Message;
import com.example.real.tool.TimeTextTool;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class RecyclerViewAdapterForMessages extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int CONTENTUSER = 0;
    private final int USER = 1;
    private int flag;

    private ArrayList<Message> messageList;
    private Context context;
    private FirebaseUser user;
    private Callback callback;

    private Bitmap contentUserProfileImageBitmap;
    private Bitmap userProfileImageBitmap;
    private String contentUserProfileNickName;
    private String userProfileNickName;

    private Bitmap currentUserProfileImageBitmap;
    private Bitmap anotherUserProfileImageBitmap;
    private String currentUserProfileNickName;
    private String anotherUserProfileNickName;

    private String userUID;
    private String contentUID;

    public RecyclerViewAdapterForMessages(
            Context context,
            Bitmap contentUserProfileImageBitmap,
            Bitmap userProfileImageBitmap,
            String contentUserProfileNickName,
            String userProfileNickName,
            String userUID,
            String contentUID) {

        this.messageList = new ArrayList<>();
        this.context = context;

        this.contentUserProfileImageBitmap = contentUserProfileImageBitmap;
        this.userProfileImageBitmap = userProfileImageBitmap;
        this.contentUserProfileNickName = contentUserProfileNickName;
        this.userProfileNickName = userProfileNickName;
        this.userUID = userUID;
        this.contentUID = contentUID;

        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user.getUid().equals(userUID)){
            currentUserProfileImageBitmap = userProfileImageBitmap;
            currentUserProfileNickName = userProfileNickName;
            anotherUserProfileImageBitmap = contentUserProfileImageBitmap;
            anotherUserProfileNickName = contentUserProfileNickName;
        }else{
            currentUserProfileImageBitmap = contentUserProfileImageBitmap;
            currentUserProfileNickName = contentUserProfileNickName;
            anotherUserProfileImageBitmap = userProfileImageBitmap;
            anotherUserProfileNickName = userProfileNickName;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new RecyclerViewAdapterForMessages.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {

        Message tempMessage = messageList.get(position);

        String uid = tempMessage.getFromUid();
        String message = tempMessage.getMessage();
        String time = tempMessage.getTime();

        //TimeTextTool timeTextTool = new TimeTextTool(time);

        if(message.isEmpty()==false && message!=null) {
            RecyclerViewAdapterForMessages.MyViewHolder myViewHolder = (RecyclerViewAdapterForMessages.MyViewHolder) holder;

            if (user.getUid().equals(uid)) {
                myViewHolder.MessageLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                myViewHolder.MessageTextView.setText(message);
                myViewHolder.MessageTimeTextView.setText(time);
                myViewHolder.MessageUserProfileNameTextView.setText(currentUserProfileNickName);
                myViewHolder.MessageUserProfileImgView.setImageBitmap(currentUserProfileImageBitmap);
            } else {
                myViewHolder.MessageLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                myViewHolder.MessageTextView.setText(message);
                myViewHolder.MessageTimeTextView.setText(time);
                myViewHolder.MessageUserProfileNameTextView.setText(anotherUserProfileNickName);
                myViewHolder.MessageUserProfileImgView.setImageBitmap(anotherUserProfileImageBitmap);
            }

        }
    }

    public void addItem(Message message){
        messageList.add(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout MessageLinearLayout;
        ImageView MessageUserProfileImgView;
        TextView MessageUserProfileNameTextView;
        TextView MessageTextView;
        TextView MessageTimeTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            MessageLinearLayout = itemView.findViewById(R.id.messageItemLinearLayout);
            MessageUserProfileImgView = itemView.findViewById(R.id.messageItemProfileImageView);
            MessageUserProfileNameTextView = itemView.findViewById(R.id.messageItemProfileNickNameTextView);
            MessageTextView = itemView.findViewById(R.id.messageItemMessageTextView);
            MessageTimeTextView = itemView.findViewById(R.id.messageItemTimeTextView);
        }
    }
}
