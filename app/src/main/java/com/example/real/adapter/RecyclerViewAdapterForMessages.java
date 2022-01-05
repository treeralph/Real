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

    public RecyclerViewAdapterForMessages(
            Context context,
            Bitmap contentUserProfileImageBitmap,
            Bitmap userProfileImageBitmap,
            String contentUserProfileNickName,
            String userProfileNickName) {

        this.messageList = new ArrayList<>();
        this.context = context;

        this.contentUserProfileImageBitmap = contentUserProfileImageBitmap;
        this.userProfileImageBitmap = userProfileImageBitmap;
        this.contentUserProfileNickName = contentUserProfileNickName;
        this.userProfileNickName = userProfileNickName;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item, parent, false);
        return new RecyclerViewAdapterForMessages.MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();

        Message tempMessage = messageList.get(position);
        String uid = tempMessage.getToUid();
        String message = tempMessage.getMessage();
        String time = tempMessage.getTime();

        RecyclerViewAdapterForMessages.MyViewHolder myViewHolder = (RecyclerViewAdapterForMessages.MyViewHolder)holder;
        // 이부분이 오류가 뜨는데
        /*
        * java.lang.ClassCastException: com.example.real.adapter.RecyclerViewAdapterForContents$MyViewHolder
        * cannot be cast to com.example.real.adapter.RecyclerViewAdapterForMessages$MyViewHolder2
        *
        * */
        if(user.getUid().equals(uid)){
            myViewHolder.MessageLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            myViewHolder.MessageTextView.setText(message);
            myViewHolder.MessageTimeTextView.setText(time);
            myViewHolder.MessageUserProfileNameTextView.setText(userProfileNickName);
            myViewHolder.MessageUserProfileImgView.setImageBitmap(userProfileImageBitmap);
        }else {
            myViewHolder.MessageLinearLayout.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
            myViewHolder.MessageTextView.setText(message);
            myViewHolder.MessageTimeTextView.setText(time);
            myViewHolder.MessageUserProfileNameTextView.setText(contentUserProfileNickName);
            myViewHolder.MessageUserProfileImgView.setImageBitmap(contentUserProfileImageBitmap);
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
