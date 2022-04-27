package com.example.real.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.AuctionContentActivity;
import com.example.real.Callback;
import com.example.real.ContentActivity;
import com.example.real.R;
import com.example.real.data.Comment;
import com.example.real.data.Content;
import com.example.real.data.Contents;
import com.example.real.data.Data;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class RecyclerViewAdapterForContents extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    static final int CONTENTACTIVITY = 10;

    private ArrayList<Contents> contentsList;
    private Context context;
    private FirebaseUser user;
    static final int FLICKERNEGATIVE = -1;
    static final int FLICKERPOSITIVE = 1;

    public RecyclerViewAdapterForContents(ArrayList<Contents> contentsList, Context context) {
        this.contentsList = contentsList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contents_item_v2, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contents_item_design, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        user = FirebaseAuth.getInstance().getCurrentUser();
        String contentId = contentsList.get(position).getContentId();
        String contentType = contentsList.get(position).getContentType();
        MyViewHolder myViewHolder = (MyViewHolder)holder;

                // As click content, send content id from contents to content
        myViewHolder.ContentLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    //myViewHolder.ContentCardView.setCardBackgroundColor(000000);
                    if (contentType.equals("Content")) {

                        Bitmap bitmap = ((BitmapDrawable) myViewHolder.ContentImgView.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra("ContentId", contentId);
                        intent.putExtra("ImageBitmap", byteArray);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(myViewHolder.ContentCardView, "contentsItemImageCardView");
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                        //context.startActivity(intent, options.toBundle());
                        ((Activity) context).startActivityForResult(intent, CONTENTACTIVITY,options.toBundle());

                    } else if (contentType.equals("AuctionContent")) {

                        Bitmap bitmap = ((BitmapDrawable) myViewHolder.ContentImgView.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        Intent intent = new Intent(context, AuctionContentActivity.class);
                        intent.putExtra("ContentId", contentId);
                        intent.putExtra("ImageBitmap", byteArray);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(myViewHolder.ContentCardView, "contentsItemImageCardView");
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                        //context.startActivity(intent, options.toBundle());
                        ((Activity) context).startActivityForResult(intent, CONTENTACTIVITY, options.toBundle());


                    } else {

                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        final int[] w = {FLICKERNEGATIVE}; // 나중에 DB에서 값을 받아오게 바꾸3
        myViewHolder.ContentLikeFlicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(w[0] == FLICKERNEGATIVE){// 하트가 비어있는 상태에서 버튼을 누르면
                    // DB에 UserLog Write
                    FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                            context, "UserProfile", user.getUid());
                    firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
                        @Override
                        public void OnCallback(Object object) {
                            UserProfile userprofile = (UserProfile) object;
                            String userlog = userprofile.getUserLog();
                            String address = "Content/" + contentId;
                            if(userlog.equals("")){
                                // Create Json Obj & JsonArray
                                JsonArray frame = new JsonArray();
                                JsonObject init = new JsonObject();
                                init.addProperty("Type","Like");
                                init.addProperty("Address",address);
                                frame.add(init);
                                firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "UserLog",
                                        frame.toString(), new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {

                                            }
                                        });
                            } else{
                                // Parsing JsonArray
                                JsonParser parser = new JsonParser();
                                Object tempparsed = parser.parse(userlog);
                                JsonArray templog = (JsonArray) tempparsed;

                                // Create Json Obj
                                JsonObject temp = new JsonObject();
                                temp.addProperty("Type", "Like");
                                temp.addProperty("Address",address);

                                // Add & Update
                                templog.add(temp);
                                firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "UserLog",
                                        templog.toString(), new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {

                                            }
                                        });
                            }

                        }
                    });

                    // Write 끝나면 콜백에 w값을 바꿔주고 이미지변경
                    w[0] = FLICKERPOSITIVE;
                    myViewHolder.ContentLikeFlicker.setImageResource(R.drawable.new_heart_red);
                }
                else{                       // 하트가 차있는 상태에서 버튼을 누르면
                    // DB에 UserLog Search & Delete
                    FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                            context, "UserProfile", user.getUid());
                    firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
                        @Override
                        public void OnCallback(Object object) {
                            UserProfile userprofile = (UserProfile) object;
                            String userlog = userprofile.getUserLog();
                            String address = "Content/" + contentId;

                            // Parsing JsonArray
                            JsonParser parser = new JsonParser();
                            Object tempparsed = parser.parse(userlog);
                            JsonArray templog = (JsonArray) tempparsed;

                            // Create Json Obj
                            JsonObject temp = new JsonObject();
                            temp.addProperty("Type", "Like");
                            temp.addProperty("Address",address);

                            // Delete & Update
                            templog.remove(temp);
                            firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "UserLog",
                                    templog.toString(), new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {

                                        }
                                    });
                        }
                    });

                    // Delete 끝나면 콜백에 w값을 바꿔주고 이미지변경
                    w[0] = FLICKERNEGATIVE;
                    myViewHolder.ContentLikeFlicker.setImageResource(R.drawable.new_heart_empty);
                }

            }
        });

        FirestoreManager firestoreManagerForContent = new FirestoreManager(context, "Content", user.getUid());
        FirestoreManager firestoreManagerForComment = new FirestoreManager(context,"Comment",user.getUid());
        firestoreManagerForContent.read("Content", contentId, new Callback() {
            @Override
            public void OnCallback(Object object) {
                Content tempContent = (Content)object;

                String uid = tempContent.getUid();
                String title = tempContent.getTitle();
                String time = tempContent.getTime();

                String year = time.substring(0, 4);
                String month = time.substring(4, 6);
                String day = time.substring(6, 8);
                String hour = time.substring(8, 10);
                String min = time.substring(10, 12);

                myViewHolder.ContentTitleTextView.setText(title);
                myViewHolder.ContentTimeTextView.setText(year + "." + month + "." + day + " - " + hour + ":" + min);

                FirestoreManager firestoreManagerForUser = new FirestoreManager(context, "UserProfile", user.getUid());
                firestoreManagerForUser.read("UserProfile", user.getUid(), new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        UserProfile userprofile = (UserProfile)object;
                        String userlog = userprofile.getUserLog();
                        String address = "Content/" + contentId;

                        // Parsing JsonArray
                        JsonParser parser = new JsonParser();
                        Object tempparsed = parser.parse(userlog);
                        if(!userlog.equals("")){
                            JsonArray templog = (JsonArray) tempparsed;

                            // Search Obj in Array
                            for (JsonElement shard : templog){
                                String shardtype = shard.getAsJsonObject().get("Type").getAsString();
                                String shardaddress = shard.getAsJsonObject().get("Address").getAsString();
                                System.out.println(shardtype + " * " + shardaddress);
                                if(shardtype.equals("Like") & shardaddress.equals(address)){
                                    myViewHolder.ContentLikeFlicker.setImageResource(R.drawable.new_heart_red);
                                    w[0] = FLICKERPOSITIVE;
                                    break;
                                }
                            }
                        }

                    }
                });
                firestoreManagerForUser.read("UserProfile", uid, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        UserProfile userProfile = (UserProfile)object;
                        String nickname = userProfile.getNickName();
                        myViewHolder.ContentProfileTextView.setText(nickname);


                        StorageManager storageManagerForUserProfileImage = new StorageManager(context, "UserProfileImage", user.getUid());
                        storageManagerForUserProfileImage.downloadImg2View("UserProfileImage", uid, myViewHolder.ContentProfileImgView, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                StorageManager storageManagerForContent = new StorageManager(context, "image", user.getUid());
                                storageManagerForContent.downloadImg2View("image/" + contentId + "/100", "0", myViewHolder.ContentImgView, new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                    }
                                });
                            }
                        });
                    }
                });
                // NumComments
                firestoreManagerForComment.read("Content/" + contentId + "/Comments", new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        ArrayList<Data> EmptyList = new ArrayList<>();
                        if(object.equals(EmptyList)|| object==null){}
                        else{
                            int numcomment = 0;
                            ArrayList<Comment> commentList = (ArrayList<Comment>)object;
                            for(Comment comment : commentList){
                                int temp = 1 + Integer.parseInt(comment.getRecomment_token());
                                numcomment += temp;
                            }
                            myViewHolder.NumCommentsTextview.setText(String.valueOf(numcomment));
                        }
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return contentsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ContentLinearLayout;
        CardView ContentCardView;
        ImageView ContentImgView;
        ImageView ContentProfileImgView;
        TextView ContentTitleTextView;
        TextView ContentProfileTextView;
        TextView ContentTimeTextView;
        ImageView ContentLikeFlicker;
        TextView NumCommentsTextview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ContentLinearLayout = itemView.findViewById(R.id.contentsItemLinearLayoutDesign);
            ContentCardView = itemView.findViewById(R.id.itemCardViewDesign);
            ContentImgView = itemView.findViewById(R.id.itemImageViewDesign);
            ContentProfileImgView = itemView.findViewById(R.id.itemProfileImageImageViewDesign);
            ContentTitleTextView = itemView.findViewById(R.id.itemTitleTextViewDesign);
            ContentProfileTextView = itemView.findViewById(R.id.itemProfileInfoTextViewDesign);
            ContentTimeTextView = itemView.findViewById(R.id.itemTimeTextViewDesign);
            ContentLikeFlicker = itemView.findViewById(R.id.itemLikeFlickerDesign);
            NumCommentsTextview = itemView.findViewById(R.id.itemNumCommentsDesign);
        }
    }
}
