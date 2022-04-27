package com.example.real.adapter;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.AuctionContentActivity;
import com.example.real.Callback;
import com.example.real.ContentActivity;
import com.example.real.R;
import com.example.real.data.AuctionContent;
import com.example.real.data.Comment;
import com.example.real.data.Content;
import com.example.real.data.Contents;
import com.example.real.data.Data;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.tool.TimeTextTool;
import com.google.android.material.internal.ScrimInsetsFrameLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.zip.Inflater;

public class RecyclerViewAdapterForContentsV2 extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    public static final String TAG = "RecyclerViewAdapterForContentsV2";

    public static final int errorFlag = -1;
    public static final int contentFlag = 0;
    public static final int auctionContentFlag = 1;

    FirebaseUser user;

    FirestoreManager firestoreManagerForContent;
    FirestoreManager firestoreManagerForComment;
    StorageManager storageManagerForContent;

    Context context;
    ArrayList<Contents> contentsList;

    public RecyclerViewAdapterForContentsV2(Context context, ArrayList<Contents> contentsList) {

        this.context = context;
        this.contentsList = contentsList;

        user = FirebaseAuth.getInstance().getCurrentUser();

        firestoreManagerForContent =  new FirestoreManager(context, "Content", user.getUid());
        firestoreManagerForComment =  new FirestoreManager(context, "Comment", user.getUid());
        storageManagerForContent = new StorageManager(context, "image", user.getUid());
    }

    @Override
    public int getItemViewType(int position) {

        Contents tempContents = contentsList.get(position);

        String flag = tempContents.getContentType();
        switch (flag){
            case "Content":
                return contentFlag;
            case "AuctionContent":
                return auctionContentFlag;
            default:
                return errorFlag;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        switch (viewType){
            case contentFlag:
                return new AuctionContentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.contents_item_for_content, parent, false));
            case auctionContentFlag:
                return new AuctionContentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.contents_item_for_auction_content, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Contents tempContents = contentsList.get(position);
        String contentId = tempContents.getContentId();

        switch(getItemViewType(position)){
            case contentFlag:

                ContentViewHolder viewHolderForC = (ContentViewHolder) holder;

                viewHolderForC.contentsLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap bitmap = ((BitmapDrawable) viewHolderForC.contentsImageView.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra("ContentId", contentId);
                        intent.putExtra("ImageBitmap", byteArray);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(viewHolderForC.contentsCardView, "contentsItemForCCardView");
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                        //((Activity) context).startActivityForResult(intent, 10, options.toBundle());
                        ((Activity) context).startActivity(intent, options.toBundle());
                    }
                });

                Log.w(TAG, "Content read => " + contentId);
                firestoreManagerForContent.read("Content", contentId, new Callback() {
                    @Override
                    public void OnCallback(Object object) {

                        Content content = (Content) object;

                        // todo: time, title, price, remaining time

                        String time = content.getTime();
                        String title = content.getTitle();
                        String price = content.getPrice();
                        String location = content.getLocation();

                        TimeTextTool tTT = new TimeTextTool(time);
                        viewHolderForC.contentsTimeTextView.setText(tTT.Time2Text());
                        viewHolderForC.contentsTitleTextView.setText(title);
                        viewHolderForC.contentsPriceTextView.setText(price + "원");
                        viewHolderForC.contentsLocationTextView.setText(location);

                        // todo: read comment

                        String commentPath = "Content/" + contentId + "/Comments";
                        Log.w(TAG, "Read Content Comments => " + commentPath);
                        firestoreManagerForComment.read(commentPath, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                ArrayList<Data> EmptyList = new ArrayList<>();
                                if(object.equals(EmptyList)|| object==null){
                                    viewHolderForC.contentsNumCommentTextView.setText("0");
                                }
                                else{
                                    int numcomment = 0;
                                    ArrayList<Comment> commentList = (ArrayList<Comment>)object;
                                    for(Comment comment : commentList){
                                        int temp = 1 + Integer.parseInt(comment.getRecomment_token());
                                        numcomment += temp;
                                    }
                                    viewHolderForC.contentsNumCommentTextView.setText(String.valueOf(numcomment));
                                }

                                String contentImagePath = "image/" + contentId + "/100";
                                Log.w(TAG, "Read Content Image => " + contentImagePath);
                                storageManagerForContent.downloadImg2View(contentImagePath, "0", viewHolderForC.contentsImageView, new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        Log.w(TAG, "Content: " + contentId + " load Success");
                                    }
                                });

                            }
                        });
                    }
                });
                break;

            case auctionContentFlag:

                AuctionContentViewHolder viewHolderForAC = (AuctionContentViewHolder) holder;

                viewHolderForAC.contentsLinearLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Bitmap bitmap = ((BitmapDrawable) viewHolderForAC.contentsImageView.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();

                        Intent intent = new Intent(context, AuctionContentActivity.class);
                        intent.putExtra("ContentId", contentId);
                        intent.putExtra("ImageBitmap", byteArray);

                        Pair[] pairs = new Pair[1];
                        pairs[0] = new Pair<View, String>(viewHolderForAC.contentsCardView, "contentsItemForACCardView");
                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);

                        //((Activity) context).startActivityForResult(intent, 10, options.toBundle());
                        ((Activity) context).startActivity(intent, options.toBundle());
                    }
                });

                Log.w(TAG, "AuctionContent read => " + contentId);
                firestoreManagerForContent.read("Content", contentId, new Callback() {
                    @Override
                    public void OnCallback(Object object) {

                        AuctionContent auctionContent = (AuctionContent) object;

                        // todo: time, title, price, remaining time

                        String time = auctionContent.getTime();
                        String title = auctionContent.getTitle();
                        String price = auctionContent.getPrice();
                        String auctionEndTime = auctionContent.getAuctionEndTime();
                        String location = auctionContent.getLocation();

                        TimeTextTool tTT = new TimeTextTool(time);
                        viewHolderForAC.contentsTimeTextView.setText(tTT.Time2Text());
                        viewHolderForAC.contentsTitleTextView.setText(title);
                        viewHolderForAC.contentsPriceTextView.setText(price + "원");
                        viewHolderForAC.contentsLocationTextView.setText(location);

                        DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
                        LocalDateTime time2Local = LocalDateTime.parse(auctionEndTime, formatter);
                        LocalDateTime now = LocalDateTime.now();
                        if(now.isAfter(time2Local)){ // expired
                            viewHolderForAC.contentsRemainingTextView.setText("만료된 경매입니다.");
                        } else{ // acquired
                            TimeTextTool tTT_a = new TimeTextTool(auctionEndTime);
                            viewHolderForAC.contentsRemainingTextView.setText(tTT_a.Time3Text());
                        }

                        // todo: read comment

                        String commentPath = "Content/" + contentId + "/Comments";
                        Log.w(TAG, "Read AuctionContent Comments => " + commentPath);
                        firestoreManagerForComment.read(commentPath, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                ArrayList<Data> EmptyList = new ArrayList<>();
                                if(object.equals(EmptyList)|| object==null){
                                    viewHolderForAC.contentsNumCommentTextView.setText("0");
                                }
                                else{
                                    int numcomment = 0;
                                    ArrayList<Comment> commentList = (ArrayList<Comment>)object;
                                    for(Comment comment : commentList){
                                        int temp = 1 + Integer.parseInt(comment.getRecomment_token());
                                        numcomment += temp;
                                    }
                                    viewHolderForAC.contentsNumCommentTextView.setText(String.valueOf(numcomment));
                                }

                                String contentImagePath = "image/" + contentId + "/100";
                                Log.w(TAG, "Read AuctionContent Image => " + contentImagePath);
                                storageManagerForContent.downloadImg2View(contentImagePath, "0", viewHolderForAC.contentsImageView, new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        Log.w(TAG, "AuctionContent: " + contentId + " load Success");
                                    }
                                });

                            }
                        });
                    }
                });
                break;

            default:

                break;
        }
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ContentViewHolder extends RecyclerView.ViewHolder{

        LinearLayout contentsLinearLayout;
        CardView contentsCardView;
        ImageView contentsImageView;
        TextView contentsTimeTextView;
        TextView contentsNumCommentTextView;
        TextView contentsTitleTextView;
        TextView contentsLocationTextView;
        TextView contentsPriceTextView;

        public ContentViewHolder(@NonNull View itemView) {
            super(itemView);

            contentsLinearLayout = itemView.findViewById(R.id.contentsItemForCLinearLayout);
            contentsCardView = itemView.findViewById(R.id.contentsItemForCCardView);
            contentsImageView = itemView.findViewById(R.id.contentsItemForCImageView);
            contentsTimeTextView = itemView.findViewById(R.id.contentsItemForCTimeTextView);
            contentsNumCommentTextView = itemView.findViewById(R.id.contentsItemForCNumCommentTextView);
            contentsTitleTextView = itemView.findViewById(R.id.contentsItemForCTitleTextView);
            contentsLocationTextView = itemView.findViewById(R.id.contentsItemForCLocationTextView);
            contentsPriceTextView = itemView.findViewById(R.id.contentsItemForCPriceTextView);
        }
    }

    public static class AuctionContentViewHolder extends RecyclerView.ViewHolder{

        LinearLayout contentsLinearLayout;
        CardView contentsCardView;
        ImageView contentsImageView;
        TextView contentsTimeTextView;
        TextView contentsNumCommentTextView;
        TextView contentsTitleTextView;
        TextView contentsLocationTextView;
        TextView contentsPriceTextView;
        TextView contentsRemainingTextView;

        public AuctionContentViewHolder(@NonNull View itemView) {
            super(itemView);

            contentsLinearLayout = itemView.findViewById(R.id.contentsItemForACLinearLayout);
            contentsCardView = itemView.findViewById(R.id.contentsItemForACCardView);
            contentsImageView = itemView.findViewById(R.id.contentsItemForACImageView);
            contentsTimeTextView = itemView.findViewById(R.id.contentsItemForACTimeTextView);
            contentsNumCommentTextView = itemView.findViewById(R.id.contentsItemForACNumCommentTextView);
            contentsTitleTextView = itemView.findViewById(R.id.contentsItemForACTitleTextView);
            contentsLocationTextView = itemView.findViewById(R.id.contentsItemForACLocationTextView);
            contentsPriceTextView = itemView.findViewById(R.id.contentsItemForACPriceTextView);
            contentsRemainingTextView = itemView.findViewById(R.id.contentsItemForACRemainingTimeTextView);
        }
    }
}
