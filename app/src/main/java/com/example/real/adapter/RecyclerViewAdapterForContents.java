package com.example.real.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.transition.AutoTransition;
import android.transition.Scene;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.real.AuctionContentActivity;
import com.example.real.Callback;
import com.example.real.ContentActivity;
import com.example.real.ContentsActivity;
import com.example.real.R;
import com.example.real.data.Content;
import com.example.real.data.Contents;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class RecyclerViewAdapterForContents extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<Contents> contentsList;
    private Context context;
    private FirebaseUser user;

    public RecyclerViewAdapterForContents(ArrayList<Contents> contentsList, Context context) {
        this.contentsList = contentsList;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contents_item_v2, parent, false);
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contents_itme_design, parent, false);
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

                if(contentType.equals("Content")) {

                    Bitmap bitmap = ((BitmapDrawable)myViewHolder.ContentImgView.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(context, ContentActivity.class);
                    intent.putExtra("ContentId", contentId);
                    intent.putExtra("ImageBitmap", byteArray);

                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(myViewHolder.ContentImgView, "contentsItemImageCardView");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                    context.startActivity(intent, options.toBundle());

                } else if(contentType.equals("AuctionContent")){

                    Bitmap bitmap = ((BitmapDrawable)myViewHolder.ContentImgView.getDrawable()).getBitmap();
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    Intent intent = new Intent(context, AuctionContentActivity.class);
                    intent.putExtra("ContentId", contentId);
                    intent.putExtra("ImageBitmap", byteArray);

                    Pair[] pairs = new Pair[1];
                    pairs[0] = new Pair<View, String>(myViewHolder.ContentImgView, "contentsItemImageCardView");
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                    context.startActivity(intent, options.toBundle());

                } else{

                }
            }
        });


        FirestoreManager firestoreManagerForContent = new FirestoreManager(context, "Content", user.getUid());
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
            }
        });
    }


    @Override
    public int getItemCount() {
        return contentsList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ContentLinearLayout;
        ImageView ContentImgView;
        ImageView ContentProfileImgView;
        TextView ContentTitleTextView;
        TextView ContentProfileTextView;
        TextView ContentTimeTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            ContentLinearLayout = itemView.findViewById(R.id.contentsItemLinearLayoutDesign);
            ContentImgView = itemView.findViewById(R.id.itemImageViewDesign);
            ContentProfileImgView = itemView.findViewById(R.id.itemProfileImageImageViewDesign);
            ContentTitleTextView = itemView.findViewById(R.id.itemTitleTextViewDesign);
            ContentProfileTextView = itemView.findViewById(R.id.itemProfileInfoTextViewDesign);
            ContentTimeTextView = itemView.findViewById(R.id.itemTimeTextViewDesign);
        }
    }
}
