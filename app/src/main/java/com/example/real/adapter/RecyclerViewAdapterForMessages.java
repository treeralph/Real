package com.example.real.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.Callback;
import com.example.real.ChattingActivity;
import com.example.real.R;
import com.example.real.data.Message;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.RealTimeDatabaseManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.tool.TimeTextTool;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import static com.example.real.adapter.RecyclerViewAdapterForChattingRoom.TAG;

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
    private String databasePath;

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
            String contentUID,
            String databasePath) {

        this.messageList = new ArrayList<>();
        this.context = context;

        this.contentUserProfileImageBitmap = contentUserProfileImageBitmap;
        this.userProfileImageBitmap = userProfileImageBitmap;
        this.contentUserProfileNickName = contentUserProfileNickName;
        this.userProfileNickName = userProfileNickName;
        this.userUID = userUID;
        this.contentUID = contentUID;
        this.databasePath = databasePath;
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
        if(message.getFromUid() == null || message.getFromUid().isEmpty()){
            if(message.getIsconfirmed() == Boolean.FALSE){
                // Not yet Confirmed
                Log.d("NOWNOWNOWNOW_VIEWTYPE", "100");
                return 100;
            }
            else{
                // Confirmed
                Log.d("NOWNOWNOWNOW_VIEWTYPE", "101");
                return 101;
            }
        }
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
            case 100:
                Log.d("NOWNOWNOWNOW_VIEWHOLDERTYPE", "100");
                return new AppointmentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_for_appointment, parent, false));
            case 101:
                Log.d("NOWNOWNOWNOW_VIEWHOLDERTYPE", "101");
                return new ConfirmedAppointmentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_for_confirmed_appointment, parent, false));
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

        String uri = msg.getImageUri();

        String time = msg.getTime();
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
            case 100:
                Log.d("NOWNOWNOWNOW_MESSAGETYPE", "100");

                AppointmentViewHolder ap_viewHolder = (AppointmentViewHolder) holder;
                ap_viewHolder.MessageForAppointmentTimeTextView.setText(msg.getReservedTime());
                ap_viewHolder.MessageForAppointmentLocationTextView.setText(msg.getLocation());
                ap_viewHolder.MessageForAppointmentPositiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Upload data to firebase where chatting room exists
                        Message message = new Message(msg.getReservedTime(), msg.getLocation(),true);
                        RealTimeDatabaseManager realTimeDatabaseManager = new RealTimeDatabaseManager(context, "Messages", user.getUid());
                        realTimeDatabaseManager.writeMessage(databasePath, message);
                        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(context, "UserProfile", user.getUid());
                        if (user.getUid().equals(userUID)) {
                            String fromUserUid = userUID;
                            String toUserUid = contentUID;
                            firestoreManagerForUserProfile.read("UserProfile", fromUserUid, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    UserProfile fromUser = (UserProfile) object;
                                    ArrayList<String> ChattingRoomID = fromUser.getChattingRoomID();
                                    if (ChattingRoomID.contains(databasePath) == false) {
                                        ChattingRoomID.add(databasePath);
                                        firestoreManagerForUserProfile.update("UserProfile", fromUserUid, "ChattingRoomID", ChattingRoomID, new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {
                                                int LogMessage = (int) object;
                                                if (LogMessage == 0) {
                                                    Log.d(TAG, "ISSUCCESSFUL");
                                                } else {
                                                    Log.d(TAG, "ISFAILURE");
                                                }
                                                firestoreManagerForUserProfile.read("UserProfile", toUserUid, new Callback() {
                                                    @Override
                                                    public void OnCallback(Object object) {
                                                        UserProfile fromUser = (UserProfile) object;
                                                        ArrayList<String> ChattingRoomID = fromUser.getChattingRoomID();
                                                        if (ChattingRoomID.contains(databasePath) == false) {
                                                            ChattingRoomID.add(databasePath);
                                                            firestoreManagerForUserProfile.update("UserProfile", toUserUid, "ChattingRoomID", ChattingRoomID, new Callback() {
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
                                            }
                                        });
                                    }
                                }
                            });
                        } else {
                            String fromUserUid = contentUID;
                            String toUserUid = userUID;
                            firestoreManagerForUserProfile.read("UserProfile", fromUserUid, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    UserProfile fromUser = (UserProfile) object;
                                    ArrayList<String> ChattingRoomID = fromUser.getChattingRoomID();
                                    if (ChattingRoomID.contains(databasePath) == false) {
                                        ChattingRoomID.add(databasePath);
                                        firestoreManagerForUserProfile.update("UserProfile", fromUserUid, "ChattingRoomID", ChattingRoomID, new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {
                                                int LogMessage = (int) object;
                                                if (LogMessage == 0) {
                                                    Log.d(TAG, "ISSUCCESSFUL");
                                                } else {
                                                    Log.d(TAG, "ISFAILURE");
                                                }
                                                firestoreManagerForUserProfile.read("UserProfile", toUserUid, new Callback() {
                                                    @Override
                                                    public void OnCallback(Object object) {
                                                        UserProfile fromUser = (UserProfile) object;
                                                        ArrayList<String> ChattingRoomID = fromUser.getChattingRoomID();
                                                        if (ChattingRoomID.contains(databasePath) == false) {
                                                            ChattingRoomID.add(databasePath);
                                                            firestoreManagerForUserProfile.update("UserProfile", toUserUid, "ChattingRoomID", ChattingRoomID, new Callback() {
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
                                            }
                                        });
                                    }
                                }
                            });
                        }
                        // and then, delete this item from recyclerview
                        messageList.remove(position);
                        notifyItemRemoved(position);
                        // and then, add item without server to get Appointment
                        Toast.makeText(context, "asdf", Toast.LENGTH_SHORT).show();
                    }
                });
                ap_viewHolder.MessageForAppointmentNegativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Jst delete this item from recyclerview
                        messageList.remove(position);
                        notifyItemRemoved(position);
                        // and then , refresh recyclerview to check item deleted
                        Toast.makeText(context, "qwer", Toast.LENGTH_SHORT).show();
                    }
                });
                break;
            case 101:
                Log.d("NOWNOWNOWNOW_MESSAGETYPE", "101");

                ConfirmedAppointmentViewHolder ca_viewHolder = (ConfirmedAppointmentViewHolder) holder;
                ca_viewHolder.messageItemForConfirmedAppointmentTTextView.setText(msg.getReservedTime());
                ca_viewHolder.messageItemForConfirmedAppointmentLocationTextView.setText(msg.getLocation());
                ca_viewHolder.messageItemForConfirmedAppointmentTimeTextView.setText(refinedTime);
                break;
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
    public static class AppointmentViewHolder extends RecyclerView.ViewHolder{

        TextView MessageForAppointmentTimeTextView;
        TextView MessageForAppointmentLocationTextView;
        CardView MessageForAppointmentPositiveBtn;
        CardView MessageForAppointmentNegativeBtn;
        public AppointmentViewHolder(@NonNull View itemView) {
            super(itemView);
            MessageForAppointmentTimeTextView = itemView.findViewById(R.id.messageItemForAppointmentTimeTextViewDesign);
            MessageForAppointmentLocationTextView = itemView.findViewById(R.id.messageItemForAppointmentLocationTextView2Design);
            MessageForAppointmentPositiveBtn = itemView.findViewById(R.id.messageItemForAppointmentPositiveBtn);
            MessageForAppointmentNegativeBtn = itemView.findViewById(R.id.messageItemForAppointmentNegativeBtn);


        }
    }
    public static class ConfirmedAppointmentViewHolder extends RecyclerView.ViewHolder{

        TextView messageItemForConfirmedAppointmentTTextView;
        TextView messageItemForConfirmedAppointmentLocationTextView;
        TextView messageItemForConfirmedAppointmentTimeTextView;
        public ConfirmedAppointmentViewHolder(@NonNull View itemView) {
            super(itemView);

            messageItemForConfirmedAppointmentTTextView = itemView.findViewById(R.id.messageItemForConfirmedAppointmentTTextViewDesign);
            messageItemForConfirmedAppointmentLocationTextView = itemView.findViewById(R.id.messageItemForConfirmedAppointmentLocationTextView2Design);
            messageItemForConfirmedAppointmentTimeTextView = itemView.findViewById(R.id.messageItemForConfirmedAppointmentTimeTextViewDesign);
        }
    }
}
