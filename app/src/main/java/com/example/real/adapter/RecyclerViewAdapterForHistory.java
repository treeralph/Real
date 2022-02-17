package com.example.real.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.AuctionContentActivity;
import com.example.real.Callback;
import com.example.real.ContentActivity;
import com.example.real.R;
import com.example.real.data.AuctionContent;
import com.example.real.data.Comment;
import com.example.real.data.Content;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.tool.TimeTextTool;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.sql.Time;
import java.util.List;
import java.util.zip.Inflater;

public class RecyclerViewAdapterForHistory extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<String> data;
    private Context context;
    private FirebaseUser user;

    private FirestoreManager firestoreManagerForUserProfile;
    private FirestoreManager firestoreManagerForContent;
    private FirestoreManager firestoreManagerForComment;
    private FirestoreManager firestoreManagerForAuctionContent;
    private StorageManager storageManagerForContent;
    private StorageManager storageManagerForUserProfileImg;


    public RecyclerViewAdapterForHistory(List<String> data, Context context) {
        this.data = data;
        this.context = context;
        this.user = FirebaseAuth.getInstance().getCurrentUser();

        storageManagerForContent = new StorageManager(context, "Image", user.getUid());
        storageManagerForUserProfileImg = new StorageManager(context,"UserProfileImage", user.getUid());
        firestoreManagerForComment = new FirestoreManager(context, "Comment", user.getUid());
        firestoreManagerForContent = new FirestoreManager(context, "Content", user.getUid());
        firestoreManagerForAuctionContent = new FirestoreManager(context, "AuctionContent", user.getUid());
        firestoreManagerForUserProfile = new FirestoreManager(context, "UserProfile", user.getUid());

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
        } else if (DatumSplit[0].equals("Like")){

            Log.d("TRACKING4", Address);
            return 4;
        } else{
            // error
            return 5;
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
            case 4:
                return new ContentViewHolder(
                        LayoutInflater.from(parent.getContext()).inflate(R.layout.logitem_for_content, parent, false));
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        String datum = data.get(position);
        String[] datumSplit = datum.split("#");
        String address = datumSplit[1];
        String[] addressSplit = address.split("/");
        //String datumType = datumSplit[0];
        String datumType = addressSplit[0];
        String contentId = addressSplit[1];

        Log.d("VIEWTYPE", String.valueOf(getItemViewType(position)));

        switch (getItemViewType(position)){
            case 1: // type - Content
                Log.d("TRACKING1-2", "why");
                ContentViewHolder contentViewHolder = (ContentViewHolder) holder;

                Log.d("HEREHERE", "여기 사람있어요");
                System.out.println(datumType);
                String path = "image/"+ contentId + "/100/0";
                Log.d("park",path);
                firestoreManagerForContent.read(datumType, contentId, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        Content content = (Content) object;
                        TimeTextTool timeTextTool = new TimeTextTool(content.getTime());
                        contentViewHolder.logitemforcontent_ContentTitle.setText(content.getTitle());
                        contentViewHolder.logitemforcontent_ContentDescription.setText(content.getContent());
                        contentViewHolder.logitemforcontent_ContentTime.setText(timeTextTool.Time2Text());
                        storageManagerForContent.downloadImg2View(path, contentViewHolder.logitemforcontent_ContentImg, new Callback() {
                            @Override
                            public void OnCallback(Object object) {

                            }
                        });
                    }
                }, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        data.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, data.size());
                    }
                });

                contentViewHolder.logitemforcontent_Mainlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bitmap = ((BitmapDrawable) contentViewHolder.logitemforcontent_ContentImg.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        Intent intent = new Intent(context, ContentActivity.class);
                        intent.putExtra("ContentId", contentId);
                        intent.putExtra("ImageBitmap", byteArray);

                        context.startActivity(intent);
                    }
                });
                break;

            case 2: // type - AuctionContent
                Log.d("TRACKING2-2", "why");

                AuctionContentViewHolder auctionContentViewHolder = (AuctionContentViewHolder) holder;
                String auctionpath = "image/"+ contentId + "/100/0";
                System.out.println(contentId);
                storageManagerForContent.downloadImg2View( auctionpath, auctionContentViewHolder.logitemforauctioncontent_AuctionContentImg, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        firestoreManagerForAuctionContent.read(datumType, contentId, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                System.out.println(datumType);
                                AuctionContent auctionContent = (AuctionContent) object;
                                TimeTextTool timeTextTool = new TimeTextTool(auctionContent.getTime());
                                auctionContentViewHolder.logitemforauctioncontent_AuctionContentTitle.setText(auctionContent.getTitle());
                                auctionContentViewHolder.logitemforauctioncontent_AuctionContentDescription.setText(auctionContent.getContent());
                                auctionContentViewHolder.logitemforauctioncontent_AuctionContentTime.setText(timeTextTool.Time2Text());
                            }
                        });
                    }
                });
                auctionContentViewHolder.logitemforauctioncontent_Mainlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Bitmap bitmap = ((BitmapDrawable) auctionContentViewHolder.logitemforauctioncontent_AuctionContentImg.getDrawable()).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] byteArray = stream.toByteArray();
                        Intent intent = new Intent(context, AuctionContentActivity.class);
                        intent.putExtra("ContentId", contentId);
                        intent.putExtra("ImageBitmap", byteArray);

                        context.startActivity(intent);
                    }
                });
                break;

            case 3: // type - Comment
                Log.d("TRACKING3-2", "why");

                CommentViewHolder commentViewHolder = (CommentViewHolder) holder;
                System.out.println(datumType);
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
                        storageManagerForUserProfileImg.downloadImg2View("UserProfileImage/" + comment.getTo(), commentViewHolder.logitemforcomment_ToProfileImg, new Callback() {
                            @Override
                            public void OnCallback(Object object) {

                            }
                        });
                        firestoreManagerForUserProfile.read("UserProfile", comment.getTo(), new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                UserProfile CurrentUserProfile = (UserProfile)object;
                                commentViewHolder.logitemforcomment_To.setText("@" + CurrentUserProfile.getNickName());
                            }
                        });

                        TimeTextTool timeTextTool = new TimeTextTool(comment.getTime());
                        commentViewHolder.logitemforcomment_Mention.setText(comment.getMention());
                        commentViewHolder.logitemforcomment_CommentTime.setText(timeTextTool.Time2Text());
                    }
                });
                commentViewHolder.logitemforcomment_Mainlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DocumentReference ref = FirebaseFirestore.getInstance().document("Content/" + contentId);
                        System.out.println(ref.toString());
                        ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()){
                                    DocumentSnapshot document = task.getResult();
                                    if (document.getString("contentType").equals("Content")){
                                        Intent intent = new Intent(context, ContentActivity.class);
                                        intent.putExtra("ContentId", contentId);
                                        context.startActivity(intent); }
                                    else if(document.getString("contentType").equals("AuctionContent")){
                                        Intent intent = new Intent(context, AuctionContentActivity.class);
                                        intent.putExtra("ContentId", contentId);
                                        context.startActivity(intent); }
                                }
                                else{}
                            }
                        });
                    }
                });
                break;
            case 4: // type - Like ( orthogonal )

                ContentViewHolder contentViewHolder2 = (ContentViewHolder) holder;

                Log.d("HEREHERE", "여기 사람있어요");
                System.out.println(datumType);
                String likepath = "image/"+ contentId + "/100/0";
                Log.d("park",likepath);
                storageManagerForContent.downloadImg2View(likepath, contentViewHolder2.logitemforcontent_ContentImg, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        firestoreManagerForContent.read(datumType, contentId, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                Content content = (Content) object;
                                TimeTextTool timeTextTool = new TimeTextTool(content.getTime());
                                contentViewHolder2.logitemforcontent_ContentTitle.setText(content.getTitle());
                                contentViewHolder2.logitemforcontent_ContentDescription.setText(content.getContent());
                                contentViewHolder2.logitemforcontent_ContentTime.setText(timeTextTool.Time2Text());
                            }
                        });
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
        LinearLayout logitemforcontent_Mainlayout;

        public ContentViewHolder(View itemView) {
            super(itemView);

            logitemforcontent_ContentImg = itemView.findViewById(R.id.logitemforcontent_ContentImg);
            logitemforcontent_ContentTitle = itemView.findViewById(R.id.logitemforcontent_ContentTitle);
            logitemforcontent_ContentDescription = itemView.findViewById(R.id.logitemforcontent_ContentDescription);
            logitemforcontent_ContentTime = itemView.findViewById(R.id.logitemforcontent_ContentTime);
            logitemforcontent_Mainlayout = itemView.findViewById(R.id.logitemforcontent_mainlayout);
        }
    }

    class AuctionContentViewHolder extends RecyclerView.ViewHolder{

        ImageView logitemforauctioncontent_AuctionContentImg;
        TextView logitemforauctioncontent_AuctionContentTitle;
        TextView logitemforauctioncontent_AuctionContentDescription;
        TextView logitemforauctioncontent_AuctionContentTime;
        LinearLayout logitemforauctioncontent_Mainlayout;

        public AuctionContentViewHolder(View itemView) {
            super(itemView);

            logitemforauctioncontent_AuctionContentImg = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentImg);
            logitemforauctioncontent_AuctionContentTitle = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentTitle);
            logitemforauctioncontent_AuctionContentDescription = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentDescription);
            logitemforauctioncontent_AuctionContentTime = itemView.findViewById(R.id.logitemforauctioncontent_AuctionContentTime);
            logitemforauctioncontent_Mainlayout = itemView.findViewById(R.id.logitemforauctioncontent_mainlayout);
        }
    }

    class CommentViewHolder extends RecyclerView.ViewHolder{

        TextView logitemforcomment_To;
        TextView logitemforcomment_Mention;
        TextView logitemforcomment_CommentTime;
        ImageView logitemforcomment_ToProfileImg;
        LinearLayout logitemforcomment_Mainlayout;

        public CommentViewHolder(View itemView) {
            super(itemView);

            logitemforcomment_To = itemView.findViewById(R.id.logitemforcomment_To);
            logitemforcomment_Mention = itemView.findViewById(R.id.logitemforcomment_Mention);
            logitemforcomment_CommentTime = itemView.findViewById(R.id.logitemforcomment_CommentTime);
            logitemforcomment_ToProfileImg = itemView.findViewById(R.id.logitemforcomment_ToProfileImg);
            logitemforcomment_Mainlayout = itemView.findViewById(R.id.logitemforcomment_mainlayout);
        }
    }


}
