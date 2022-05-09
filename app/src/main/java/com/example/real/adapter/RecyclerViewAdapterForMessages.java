package com.example.real.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Handler;
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
import com.example.real.data.Alarm;
import com.example.real.data.Message;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.RealTimeDatabaseManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.tool.CreatePaddle;
import com.example.real.tool.TimeTextTool;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;

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
    private RealTimeDatabaseManager realTimeDatabaseManagerForMessage;
    private RealTimeDatabaseManager realTimeDatabaseManagerForScheduledTask;
    private FirestoreManager firestoreManagerForUserProfile;

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
        this.realTimeDatabaseManagerForMessage = new RealTimeDatabaseManager(context, "Messages", user.getUid());
        this.realTimeDatabaseManagerForScheduledTask = new RealTimeDatabaseManager(context, "ScheduledTask", user.getUid());
        this.firestoreManagerForUserProfile = new FirestoreManager(context, "UserProfile", user.getUid());
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messageList.get(position);

        String fUid = message.getFromUid();
        String tUid = message.getToUid();
        String imageUri = message.getImageUri();
        String msg = message.getMessage();

        String flag = message.getFlag();
        switch(flag){

            case Message.normalMessageFlag:
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
            case Message.imageMessageFlag:
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
            case Message.appointmentMessageFlag:

                String[] msgList = msg.split("/");
                String reservedTime = msgList[0];
                String location = msgList[1];
                String isconfirmed = msgList[2];

                if (isconfirmed.equals("false")) {
                    // Not yet Confirmed
                    Log.d("NOWNOWNOWNOW_VIEWTYPE", "100");
                    return 100;
                } else if(isconfirmed.equals("true")){
                    // Confirmed
                    Log.d("NOWNOWNOWNOW_VIEWTYPE", "101");
                    return 101;
                } else{
                    return 10;
                }
            case Message.chickenWinnerMessageFlag:
                Log.d("NOWNOWNOWNOW_VIEWTYPE", "200");
                return 200;
            default:
                return 10;
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
            case 200:
                Log.d("NOWNOWNOWNOW_VIEWHOLDERTYPE", "200");
                return new ChickenBidViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.message_item_for_bidend, parent, false));
            default:
                Log.d("NOWNOWNOWNOW_VIEWHOLDERTYPE", "null");
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull  RecyclerView.ViewHolder holder, int position) {

        Log.d("NOWNOWNOWNOW_MESSAGE", messageList.get(position).toString());

        Message msg = messageList.get(position);
        String message = msg.getMessage();
        String uri = msg.getImageUri();
        String time = msg.getTime();
        String refinedTime = "";
        try {
            TimeTextTool mTimeTextTool = new TimeTextTool(time);
            refinedTime = mTimeTextTool.Time2Text();
        }catch(Exception e){

        }
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
                break;
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
                break;
            case 100:
                Log.d("NOWNOWNOWNOW_MESSAGETYPE", "100");

                String[] aMessageList = message.split("/");
                String aReservedTime = aMessageList[0];
                String aLocation = aMessageList[1];
                String aIsconfirmed = aMessageList[2];

                AppointmentViewHolder ap_viewHolder = (AppointmentViewHolder) holder;
                ap_viewHolder.MessageForAppointmentTimeTextView.setText(aReservedTime);
                ap_viewHolder.MessageForAppointmentLocationTextView.setText(aLocation);

                ap_viewHolder.MessageForAppointmentPositiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String tUid;
                        String fUid;
                        if (user.getUid().equals(userUID)) {
                            fUid = userUID;
                            tUid = contentUID;
                        }else{
                            tUid = userUID;
                            fUid = contentUID;
                        }
                        firestoreManagerForUserProfile.read("UserProfile", tUid, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                UserProfile tempUserProfile = (UserProfile) object;
                                String tToken = tempUserProfile.getDeviceToken();
                                ArrayList<String> ChattingRoomID = tempUserProfile.getChattingRoomID();
                                if (ChattingRoomID.contains(databasePath) == false) {
                                    ChattingRoomID.add(databasePath);
                                    firestoreManagerForUserProfile.update("UserProfile", tUid, "ChattingRoomID", ChattingRoomID, new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {
                                            int LogMessage = (int) object;
                                            if (LogMessage == 0) {
                                                Log.d(TAG, "ISSUCCESSFUL");
                                            } else {
                                                Log.d(TAG, "ISFAILURE");
                                            }
                                            firestoreManagerForUserProfile.read("UserProfile", fUid, new Callback() {
                                                @Override
                                                public void OnCallback(Object object) {
                                                    UserProfile tempUserProfile = (UserProfile) object;
                                                    String fToken = tempUserProfile.getDeviceToken();
                                                    ArrayList<String> ChattingRoomID = tempUserProfile.getChattingRoomID();
                                                    if (ChattingRoomID.contains(databasePath) == false) {
                                                        ChattingRoomID.add(databasePath);
                                                        firestoreManagerForUserProfile.update("UserProfile", fUid, "ChattingRoomID", ChattingRoomID, new Callback() {
                                                            @Override
                                                            public void OnCallback(Object object) {
                                                                int LogMessage = (int) object;
                                                                if (LogMessage == 0) {
                                                                    Log.d(TAG, "ISSUCCESSFUL");
                                                                } else {
                                                                    Log.d(TAG, "ISFAILURE");
                                                                }

                                                                String payload = aReservedTime + "/" + aLocation + "/true";
                                                                Message message = new Message(Message.appointmentMessageFlag, fUid, tUid, payload, fToken, tToken, "");
                                                                realTimeDatabaseManagerForMessage.writeMessage(databasePath, message);
                                                                Toast.makeText(context, "why doexntwrokasdfkasdf;", Toast.LENGTH_SHORT).show();

                                                                // todo: complement Alarm for appointment and reputation.
                                                                //realTimeDatabaseManagerForScheduledTask.writeMessage(Alarm.databasePath, new Alarm());
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
                        // and then, delete this item from recyclerview
                        messageList.remove(position);
                        notifyItemRemoved(position);
                        // and then, add item without server to get Appointment
                        //Toast.makeText(context, "asdf", Toast.LENGTH_SHORT).show();
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

                String[] caMessageList = message.split("/");
                String caReservedTime = caMessageList[0];
                String caLocation = caMessageList[1];
                String caIsconfirmed = caMessageList[2];

                ConfirmedAppointmentViewHolder ca_viewHolder = (ConfirmedAppointmentViewHolder) holder;
                ca_viewHolder.messageItemForConfirmedAppointmentTTextView.setText(caReservedTime);
                ca_viewHolder.messageItemForConfirmedAppointmentLocationTextView.setText(caLocation);
                ca_viewHolder.messageItemForConfirmedAppointmentTimeTextView.setText(refinedTime);
                break;

            case 200:
                ChickenBidViewHolder chickenBidViewHolder = (ChickenBidViewHolder) holder;

                ImageView tempImageView = chickenBidViewHolder.messageItemForUserProfilePaddle;
                TextView tempTextView = chickenBidViewHolder.messageItemForUserProfileBidPriceTextView;

                String userUid = user.getUid();
                String[] cMessageList = message.split("/");
                String contentUid = cMessageList[0];
                String bidPrice = cMessageList[1];

                tempTextView.setText(bidPrice);

                CreatePaddle createPaddle = new CreatePaddle(context, userUid);
                createPaddle.Initializer(contentUid, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        List<Bitmap> bitmapList = (List<Bitmap>) object;
                        tempImageView.post(new Runnable() {
                            @Override
                            public void run() {
                                int Paddle_Size_x = tempImageView.getWidth();
                                Bitmap InitialPaddle = createPaddle.createPaddle(bitmapList.get(0),bitmapList.get(1),bitmapList.get(2),Paddle_Size_x);

                                tempImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                                tempImageView.setAdjustViewBounds(true);
                                tempImageView.setImageBitmap(InitialPaddle);
                            }
                        });
                    }
                });
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

    public static class ChickenBidViewHolder extends RecyclerView.ViewHolder{

        ImageView messageItemForUserProfilePaddle;
        TextView messageItemForUserProfileBidPriceTextView;
        public ChickenBidViewHolder(@NonNull View itemView) {
            super(itemView);
            messageItemForUserProfilePaddle = itemView.findViewById(R.id.messageItemForBidEndUserProfilePaddle);
            messageItemForUserProfileBidPriceTextView = itemView.findViewById(R.id.messageItemForBidEndBidPriceTextView);
        }
    }
}
