package com.example.real.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.Callback;
import com.example.real.R;
import com.example.real.data.AuctionContent;
import com.example.real.data.Comment;
import com.example.real.data.Content;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.tool.TimeTextTool;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

import java.sql.Time;
import java.util.List;
import java.util.zip.Inflater;

public class RecyclerViewAdapterForHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> data;
    private Context context;
    private FirebaseUser user;

    private FirestoreManager firestoreManagerForContent;
    private FirestoreManager firestoreManagerForComment;
    private StorageManager storageManagerForContent;

    public RecyclerViewAdapterForHistory(List<String> data, Context context) {
        this.data = data;
        this.context = context;
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        storageManagerForContent = new StorageManager(context, "Image", user.getUid());
        firestoreManagerForComment = new FirestoreManager(context, "Comment", user.getUid());
        firestoreManagerForContent = new FirestoreManager(context, "Content", user.getUid());

        // data looks like
        // [ "Content/MfnsDaivaiyedaOpTxdp/Comments/20220110163124295" , ~~~ ]
    }

    public void AddItem(List<String> tempString){
        data.addAll(tempString);
        Log.d("ERROR_CHECK_FOR_ADDALL", data.toString());
    }

    @Override
    public int getItemViewType(int position) {
        String datum = data.get(position);
        String[] DatumSplit = datum.split("#");
        String Address = DatumSplit[1];

        Log.d("ADDRESSADDRESS", datum);

        if (DatumSplit[0].equals("Content")){
            // CONTENT
            Log.d("TRACKING1", Address);
            return 1;
        } else if (DatumSplit[0].equals("AuctionContent")){
            // AUCTIONCONTENT
            Log.d("TRACKING2", Address);
            return 2;
        } else if (DatumSplit[0].equals("Comment")){
            // COMMENT
            Log.d("TRACKING3", Address);
            return 3;
        } else{
            // error
            return 4;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch(viewType){
            case 1:
                Log.d("TRACKING1-1", "why");
                return new ContentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.logitem_for_content, parent, false));

            case 2:
                Log.d("TRACKING2-1", "why");
                return new AuctionContentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.logitem_for_auctioncontent, parent, false));

            case 3:
                Log.d("TRACKING3-1", "why");
                return new CommentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.logitem_for_comment, parent, false));
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String datum = data.get(position);
        String[] datumSplit = datum.split("#");
        String address = datumSplit[1];
        String[] addressSplit = address.split("/");
        String datumType = addressSplit[0];
        String contentId = addressSplit[1];

        Log.d("VIEWTYPE", String.valueOf(getItemViewType(position)));

        switch (getItemViewType(position)){
            case 1: // type - Content
                Log.d("TRACKING1-2", "why");
                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;

                Log.d("HEREHERE", "여기 사람있어요");

                String path = "image/"+ contentId + "/100/0";
                Log.d("park",path);
                storageManagerForContent.downloadImg2View(path, contentViewHolder.logitemforcontent_ContentImg, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        firestoreManagerForContent.read(datumType, contentId, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                Content content = (Content) object;
                                TimeTextTool timeTextTool = new TimeTextTool(content.getTime());
                                contentViewHolder.logitemforcontent_ContentTitle.setText(content.getTitle());
                                contentViewHolder.logitemforcontent_ContentDescription.setText(content.getContent());
                                contentViewHolder.logitemforcontent_ContentTime.setText(timeTextTool.Time2Text());
                            }
                        });
                    }
                });
                break;

            case 2: // type - AuctionContent
                Log.d("TRACKING2-2", "why");

                AuctionContentViewHolder auctionContentViewHolder = (AuctionContentViewHolder) holder;

                storageManagerForContent.downloadImg2View("image", contentId, auctionContentViewHolder.logitemforauctioncontent_AuctionContentImg, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        firestoreManagerForContent.read(datumType, contentId, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                AuctionContent auctionContent = (AuctionContent) object;
                                TimeTextTool timeTextTool = new TimeTextTool(auctionContent.getTime());
                                auctionContentViewHolder.logitemforauctioncontent_AuctionContentTitle.setText(auctionContent.getTitle());
                                auctionContentViewHolder.logitemforauctioncontent_AuctionContentDescription.setText(auctionContent.getContent());
                                auctionContentViewHolder.logitemforauctioncontent_AuctionContentTime.setText(timeTextTool.Time2Text());
                            }
                        });
                    }
                });
                break;

            case 3: // type - Comment
                Log.d("TRACKING3-2", "why");

                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;

                String subPath = "";
                for (int i = 1; i < addressSplit.length; i++) {
                    subPath += addressSplit[i] + "/";
                }

                Log.d("ERRORHAPPEN", datumType);
                Log.d("ERRORHAPPEN", subPath);

                firestoreManagerForComment.read(datumType, subPath, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        Comment comment = (Comment) object;
                        TimeTextTool timeTextTool = new TimeTextTool(comment.getTime());
                        commentViewHolder.logitemforcomment_To.setText(comment.getTo());
                        commentViewHolder.logitemforcomment_Mention.setText(comment.getMention());
                        commentViewHolder.logitemforcomment_CommentTime.setText(timeTextTool.Time2Text());
                    }
                });

                break;

            default:
                Log.e("RECYCLERVIEWADAPTERFORHISTORY_ONBINDVIEWHOLDER", "NON_CASE_ERROR");
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ContentViewHolder extends RecyclerView.ViewHolder{

        ImageView logitemforcontent_ContentImg;
        TextView logitemforcontent_ContentTitle;
        TextView logitemforcontent_ContentDescription;
        TextView logitemforcontent_ContentTime;

        public ContentViewHolder(View itemView) {
            super(itemView);

            logitemforcontent_ContentImg = itemView.findViewById(R.id.logitemforcontent_ContentImg);
            logitemforcontent_ContentTitle = itemView.findViewById(R.id.logitemforcontent_ContentTitle);
            logitemforcontent_ContentDescription = itemView.findViewById(R.id.logitemforcontent_ContentDescription);
            logitemforcontent_ContentTime = itemView.findViewById(R.id.logitemforcontent_ContentTime);
        }
    }

    class AuctionContentViewHolder extends RecyclerView.ViewHolder{

        ImageView logitemforauctioncontent_AuctionContentImg;
        TextView logitemforauctioncontent_AuctionContentTitle;
        TextView logitemforauctioncontent_AuctionContentDescription;
        TextView logitemforauctioncontent_AuctionContentTime;

        public AuctionContentViewHolder(View itemView) {
            super(itemView);

            logitemforauctioncontent_AuctionContentImg = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentImg);
            logitemforauctioncontent_AuctionContentTitle = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentTitle);
            logitemforauctioncontent_AuctionContentDescription = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentDescription);
            logitemforauctioncontent_AuctionContentTime = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentTime);
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{

        TextView logitemforcomment_To;
        TextView logitemforcomment_Mention;
        TextView logitemforcomment_CommentTime;

        public CommentViewHolder(View itemView) {
            super(itemView);

            logitemforcomment_To = itemView.findViewById(R.id.logitemforcomment_To);
            logitemforcomment_Mention = itemView.findViewById(R.id.logitemforcomment_Mention);
            logitemforcomment_CommentTime = itemView.findViewById(R.id.logitemforcomment_CommentTime);
        }
    }
}
