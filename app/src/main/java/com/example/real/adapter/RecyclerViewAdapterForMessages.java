package com.example.real.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
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
import com.example.real.data.Message;
import com.example.real.databasemanager.StorageManager;
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
    public int getItemViewType(int position) {

        Message message = messageList.get(position);
        String imageUri = message.getImageUri();
        if(imageUri == null){
            return 0;
        }else if(imageUri.isEmpty()){
            return 0;
        }else{
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case 0: return new MessageViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false));
            case 1: return new ImageViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_for_image, parent, false));
            default: return null;
        }

        /*
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new messageViewHolder(v);
         */
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {

        // holder.getItemViewType()
        switch(getItemViewType(position)) {
            case 0:
                Message mMessage = messageList.get(position);

                String mUid = mMessage.getFromUid();
                String mMsg = mMessage.getMessage();
                String mTime = mMessage.getTime();

                TimeTextTool mTimeTextTool = new TimeTextTool(mTime);
                String mRefinedTime = mTimeTextTool.Time2Text();

                if (mMsg.isEmpty() == false && mMsg != null) {
                    MessageViewHolder messageViewHolder = (MessageViewHolder) holder;

                    if (user.getUid().equals(mUid)) {
                        messageViewHolder.MessageLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        messageViewHolder.MessageTextView.setText(mMsg);
                        messageViewHolder.MessageTimeTextView.setText(mRefinedTime);
                        messageViewHolder.MessageUserProfileNameTextView.setText(currentUserProfileNickName);
                        messageViewHolder.MessageUserProfileImgView.setImageBitmap(currentUserProfileImageBitmap);
                    } else {
                        messageViewHolder.MessageLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        messageViewHolder.MessageTextView.setText(mMsg);
                        messageViewHolder.MessageTimeTextView.setText(mRefinedTime);
                        messageViewHolder.MessageUserProfileNameTextView.setText(anotherUserProfileNickName);
                        messageViewHolder.MessageUserProfileImgView.setImageBitmap(anotherUserProfileImageBitmap);
                    }

                }
            case 1:
                StorageManager storageManagerForMessageImg = new StorageManager(holder.itemView.getContext(), "MessageImage", user.getUid());

                Message iMessage = messageList.get(position);

                String iUid = iMessage.getFromUid();
                String iMsg = iMessage.getMessage();
                String iTime = iMessage.getTime();
                String iImagePath = iMessage.getImageUri();

                TimeTextTool iTimeTextTool = new TimeTextTool(iTime);
                String iRefinedTime = iTimeTextTool.Time2Text();

                if (iImagePath.isEmpty() == false && iImagePath != null) {
                    ImageViewHolder imageViewHolder = (ImageViewHolder) holder;

                    if (user.getUid().equals(iUid)) {
                        imageViewHolder.ImageLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                        imageViewHolder.ImageTimeTextView.setText(iRefinedTime);
                        imageViewHolder.ImageUserProfileNameTextView.setText(currentUserProfileNickName);
                        imageViewHolder.ImageUserProfileImgView.setImageBitmap(currentUserProfileImageBitmap);
                        storageManagerForMessageImg.downloadImg2View(iImagePath, imageViewHolder.ImageImageView, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                            }
                        });
                    } else {
                        imageViewHolder.ImageLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                        imageViewHolder.ImageTimeTextView.setText(iRefinedTime);
                        imageViewHolder.ImageUserProfileNameTextView.setText(anotherUserProfileNickName);
                        imageViewHolder.ImageUserProfileImgView.setImageBitmap(anotherUserProfileImageBitmap);
                        storageManagerForMessageImg.downloadImg2View(iImagePath, imageViewHolder.ImageImageView, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                imageViewHolder.ImageImageView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                    }
                                });
                            }
                        });
                    }

                }
            default:

        }

        /*
        Message tempMessage = messageList.get(position);

        String uid = tempMessage.getFromUid();
        String message = tempMessage.getMessage();
        String time = tempMessage.getTime();

        TimeTextTool timeTextTool = new TimeTextTool(time);
        Log.d("MESSAGE_RECYCLERVIEW_ADAPTER/TIME", time);
        String refinedTime = timeTextTool.Time2Text();
        Log.d("MESSAGE_RECYCLERVIEW_ADAPTER/REFINEDTIME", refinedTime);

        if(message.isEmpty()==false && message!=null) {
            messageViewHolder messageViewHolder = (messageViewHolder) holder;

            if (user.getUid().equals(uid)) {
                messageViewHolder.MessageLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
                messageViewHolder.MessageTextView.setText(message);
                messageViewHolder.MessageTimeTextView.setText(refinedTime);
                messageViewHolder.MessageUserProfileNameTextView.setText(currentUserProfileNickName);
                messageViewHolder.MessageUserProfileImgView.setImageBitmap(currentUserProfileImageBitmap);
            } else {
                messageViewHolder.MessageLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
                messageViewHolder.MessageTextView.setText(message);
                messageViewHolder.MessageTimeTextView.setText(refinedTime);
                messageViewHolder.MessageUserProfileNameTextView.setText(anotherUserProfileNickName);
                messageViewHolder.MessageUserProfileImgView.setImageBitmap(anotherUserProfileImageBitmap);
            }

        }
         */

    }

    public void addItem(Message message){
        messageList.add(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageViewHolder extends RecyclerView.ViewHolder{

        LinearLayout MessageLinearLayout;
        ImageView MessageUserProfileImgView;
        TextView MessageUserProfileNameTextView;
        TextView MessageTextView;
        TextView MessageTimeTextView;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);

            MessageLinearLayout = itemView.findViewById(R.id.messageItemLinearLayout);
            MessageUserProfileImgView = itemView.findViewById(R.id.messageItemProfileImageView);
            MessageUserProfileNameTextView = itemView.findViewById(R.id.messageItemProfileNickNameTextView);
            MessageTextView = itemView.findViewById(R.id.messageItemMessageTextView);
            MessageTimeTextView = itemView.findViewById(R.id.messageItemTimeTextView);
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ImageLinearLayout;
        ImageView ImageUserProfileImgView;
        TextView ImageUserProfileNameTextView;
        ImageView ImageImageView;
        TextView ImageTimeTextView;

        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageLinearLayout = itemView.findViewById(R.id.messageItemForImageLinearLayout);
            ImageUserProfileImgView = itemView.findViewById(R.id.messageItemForImageProfileImageView);
            ImageUserProfileNameTextView = itemView.findViewById(R.id.messageItemForImageProfileNickNameTextView);
            ImageImageView = itemView.findViewById(R.id.messageItemForImageImageView);
            ImageTimeTextView = itemView.findViewById(R.id.messageItemForImageTimeTextView);
        }
    }
}
