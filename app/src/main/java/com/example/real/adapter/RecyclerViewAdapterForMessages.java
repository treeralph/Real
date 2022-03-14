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

    private StorageManager storageManagerForMessageImage;

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

        this.storageManagerForMessageImage = new StorageManager(context, "MessageImage", user.getUid());
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messageList.get(position);
        String fUid = message.getFromUid();
        String tUid = message.getToUid();
        String imageUri = message.getImageUri();

        if(imageUri == null || imageUri.isEmpty()){
            if(user.getUid().equals(fUid)){
                Log.d("NOWNOWNOWNOW_VIEWTYPE", "1");
                return 1;
            }else if(user.getUid().equals(tUid)){
                Log.d("NOWNOWNOWNOW_VIEWTYPE", "0");
                return 0;
            }else{
                Log.d("NOWNOWNOWNOW_VIEWTYPE", "10");
                return 10;
            }
        }else{
            if(user.getUid().equals(fUid)){
                Log.d("NOWNOWNOWNOW_VIEWTYPE", "3");
                return 3;
            }else if(user.getUid().equals(tUid)){
                Log.d("NOWNOWNOWNOW_VIEWTYPE", "2");
                return 2;
            }else{
                Log.d("NOWNOWNOWNOW_VIEWTYPE", "10");
                return 10;
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case 0:
                Log.d("NOWNOWNOWNOW_VIEWHOLDERTYPE", "0");
                return new MessageForAnotherViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_design_for_another, parent, false));
            case 1:
                Log.d("NOWNOWNOWNOW_VIEWHOLDERTYPE", "1");
                return new MessageForUserViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_design_for_user, parent, false));
            case 2:
                Log.d("NOWNOWNOWNOW_VIEWHOLDERTYPE", "2");
                return new ImageForAnotherViewHolder(
                    LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_for_image_for_another, parent, false));
            case 3:
                Log.d("NOWNOWNOWNOW_VIEWHOLDERTYPE", "3");
                return new ImageForUserViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_for_image_for_user, parent, false));
            default:
                Log.d("NOWNOWNOWNOW_VIEWHOLDERTYPE", "null");
                return null;
        }

        /*
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new messageViewHolder(v);
         */
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {

        Log.d("NOWNOWNOWNOW_MESSAGE", messageList.get(position).toString());

        Message msg = messageList.get(position);
        String message = msg.getMessage();
        String time = msg.getTime();
        String uri = msg.getImageUri();

        TimeTextTool mTimeTextTool = new TimeTextTool(time);
        String refinedTime = mTimeTextTool.Time2Text();

        // holder.getItemViewType()
        switch(getItemViewType(position)) {
            case 0:
                Log.d("NOWNOWNOWNOW_MESSAGETYPE", "0");

                MessageForAnotherViewHolder m_viewHolder = (MessageForAnotherViewHolder) holder;
                m_viewHolder.MessageForAnotherUserProfileImgView.setImageBitmap(anotherUserProfileImageBitmap);
                m_viewHolder.MessageForAnotherTextView.setText(message);
                m_viewHolder.MessageForAnotherTimeTextView.setText(refinedTime);
                break;

            case 1:
                Log.d("NOWNOWNOWNOW_MESSAGETYPE", "1");

                MessageForUserViewHolder u_viewHolder = (MessageForUserViewHolder) holder;
                u_viewHolder.MessageForUserTextView.setText(message);
                u_viewHolder.MessageForUserTimeTextView.setText(refinedTime);
                break;

            case 2:
                Log.d("NOWNOWNOWNOW_MESSAGETYPE", "2");

                ImageForAnotherViewHolder im_viewHolder = (ImageForAnotherViewHolder) holder;
                im_viewHolder.ImageForAnotherProfileImageView.setImageBitmap(anotherUserProfileImageBitmap);
                im_viewHolder.ImageForAnotherTimeTextView.setText(refinedTime);
                storageManagerForMessageImage.downloadImg2View(uri, im_viewHolder.ImageForAnotherImageImageView, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        return;
                    }
                });

            case 3:
                Log.d("NOWNOWNOWNOW_MESSAGETYPE", "3");

                ImageForUserViewHolder iu_viewHolder = (ImageForUserViewHolder) holder;
                iu_viewHolder.ImageForUserTimeTextView.setText(refinedTime);
                storageManagerForMessageImage.downloadImg2View(uri, iu_viewHolder.ImageForUserImageImageView, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        return;
                    }
                });

            default:
                Log.d("NOWNOWNOWNOW_MESSAGETYPE", "default");
                break;

        }
    }

    public void addItem(Message message){
        messageList.add(message);
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    public static class MessageForAnotherViewHolder extends RecyclerView.ViewHolder{

        LinearLayout MessageForAnotherLinearLayout;
        ImageView MessageForAnotherUserProfileImgView;
        TextView MessageForAnotherTextView;
        TextView MessageForAnotherTimeTextView;

        public MessageForAnotherViewHolder(@NonNull View itemView) {
            super(itemView);

            MessageForAnotherLinearLayout = itemView.findViewById(R.id.messageItemForAnotherLinearLayoutDesign);
            MessageForAnotherUserProfileImgView = itemView.findViewById(R.id.messageItemForAnotherProfileImageViewDesign);
            MessageForAnotherTextView = itemView.findViewById(R.id.messageItemForAnotherMessageTextViewDesign);
            MessageForAnotherTimeTextView = itemView.findViewById(R.id.messageItemForAnotherTimeTextViewDesign);
        }
    }

    public static class MessageForUserViewHolder extends RecyclerView.ViewHolder{

        LinearLayout MessageForUserLinearLayout;
        TextView MessageForUserTextView;
        TextView MessageForUserTimeTextView;

        public MessageForUserViewHolder(@NonNull View itemView) {
            super(itemView);

            MessageForUserLinearLayout = itemView.findViewById(R.id.messageItemForUserLinearLayoutDesign);
            MessageForUserTextView = itemView.findViewById(R.id.messageItemForUserMessageTextViewDesign);
            MessageForUserTimeTextView = itemView.findViewById(R.id.messageItemForUserTimeTextViewDesign);
        }
    }

    public static class ImageForAnotherViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ImageForAnotherLinearLayout;
        ImageView ImageForAnotherProfileImageView;
        ImageView ImageForAnotherImageImageView;
        TextView ImageForAnotherTimeTextView;

        public ImageForAnotherViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageForAnotherLinearLayout = itemView.findViewById(R.id.messageItemForImageForAnotherLinearLayoutDesign);
            ImageForAnotherProfileImageView = itemView.findViewById(R.id.messageItemForImageForAnotherProfileImageViewDesign);
            ImageForAnotherImageImageView = itemView.findViewById(R.id.messageItemForImageForAnotherImageImageView);
            ImageForAnotherTimeTextView = itemView.findViewById(R.id.messageItemForImageForAnotherTimeTextViewDesign);
        }
    }

    public static class ImageForUserViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ImageForUserLinearLayout;
        ImageView ImageForUserImageImageView;
        TextView ImageForUserTimeTextView;

        public ImageForUserViewHolder(@NonNull View itemView) {
            super(itemView);

            ImageForUserLinearLayout = itemView.findViewById(R.id.messageItemForImageForUserLinearLayoutDesign);
            ImageForUserImageImageView = itemView.findViewById(R.id.messageItemForImageForUserImageImageView);
            ImageForUserTimeTextView = itemView.findViewById(R.id.messageItemForImageForUserTimeTextViewDesign);
        }
    }
}
