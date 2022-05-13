package com.example.real;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.real.adapter.ExpandableListAdapter;
import com.example.real.adapter.RecyclerViewAdapterForHistory;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserhistoryActivity extends AppCompatActivity {

    int NUM_CONTENTS;
    int NUM_COMMENTS;
    int NUM_LOVERS;
    int NUM_EARNED;

    ActivityResultLauncher<Intent> resultLauncher;
    FirebaseStorage tempstorage;
    StorageReference tempref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userhistory_design);

        // XML VARIABLES
        ImageView userprofileimg = (ImageView) findViewById(R.id.UserhistoryProfileimg);
        TextView userprofilenickname = (TextView) findViewById(R.id.UserhistoryUserNickname);
        TextView NumContents = (TextView) findViewById(R.id.UserhistoryNumContents);
        TextView NumComments = (TextView) findViewById(R.id.UserhistoryNumComments);
        TextView NUMLovers = (TextView) findViewById(R.id.UserhistoryNumLovers);
        TextView NumEarned = (TextView) findViewById(R.id.UserhistoryNumEarned); 

        LinearLayout ContentsBtn = (LinearLayout) findViewById(R.id.UserhistoryBtnContents);
        LinearLayout EarnedBtn = (LinearLayout) findViewById(R.id.UserhistoryBtnEarned);
        LinearLayout CommentsBtn = (LinearLayout) findViewById(R.id.UserhistoryBtnComments);
        LinearLayout LoversBtn = (LinearLayout) findViewById(R.id.UserhistoryBtnLovers);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.UserhistoryRecyclerViewDesign);
        RecyclerView recyclerView2 = (RecyclerView) findViewById(R.id.UserhistoryLoverRecyclerViewDesign);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        recyclerView2.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager2 = new LinearLayoutManager(this);
        recyclerView2.setLayoutManager(layoutManager2);

        LinearLayout contentsBtn = (LinearLayout) findViewById(R.id.UserhistoryActivityContentsButton);
        LinearLayout chatRoomBtn = (LinearLayout) findViewById(R.id.UserhistoryActivityChatRoomButton);
        LinearLayout userProfileBtn = (LinearLayout) findViewById(R.id.UserhistoryActivityUserProfileButton);

        // GET CURRENT USER DATA INCLUDING USERLOG
        // On callback DISCRIMINATING USERLOG

        // toolbar

        contentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        chatRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserhistoryActivity.this, ChattingRoomActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });

        userProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UserhistoryActivity.this, SetUserProfileActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });

        //
        List<String> LIST_Earned = new ArrayList<>();
        
        List<String> LIST_Contents = new ArrayList<>();
        List<String> LIST_Auctioncontents = new ArrayList<>();
        List<String> LIST_ContentsMerged = new ArrayList<>();
        
        List<String> LIST_Comments = new ArrayList<>();

        List<String> LIST_LOVERS = new ArrayList<>();
        
        List<String> LIST_DATASET = new ArrayList<>();



        RecyclerViewAdapterForHistory AdapterForHistory = new RecyclerViewAdapterForHistory(LIST_DATASET, UserhistoryActivity.this);
        RecyclerViewAdapterForHistory AdapterForLover = new RecyclerViewAdapterForHistory(LIST_LOVERS, UserhistoryActivity.this);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userUID = user.getUid();
        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                UserhistoryActivity.this, "UserProfile", user.getUid());
        StorageManager storageManagerForUserProfile = new StorageManager(
                UserhistoryActivity.this, "UserProfileImage", user.getUid());
        firestoreManagerForUserProfile.read("UserProfile", userUID, new Callback() {
            @Override
            public void OnCallback(Object object) {
                try {
                    UserProfile CurrentUserProfile = (UserProfile) object;
                    String nickname = CurrentUserProfile.getNickName();
                    userprofilenickname.setText(nickname);
                    String userlog = CurrentUserProfile.getUserLog();
                    JsonParser parser = new JsonParser();
                    Object tempparsed = parser.parse(userlog);
                    JsonArray templog = (JsonArray) tempparsed;

                    // DISCRIMINATING USERLOG
                    NUM_CONTENTS = 0;
                    NUM_COMMENTS = 0;
                    NUM_LOVERS = 0;
                    NUM_EARNED = 0;

                    for (JsonElement shard : templog) {
                        String shardtype = shard.getAsJsonObject().get("Type").getAsString();
                        String shardaddress = shard.getAsJsonObject().get("Address").getAsString();
                        //System.out.println(shardaddress.split("/")[0] + " and " + shardaddress.split("/")[1]);

                        if (shardtype.equals("Content")) {
                            firestoreManagerForUserProfile.readIgnore(shardaddress.split("/")[0], shardaddress.split("/")[1], new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    if(object != null) {
                                        NUM_CONTENTS++;
                                        LIST_Contents.add(shardtype + "#" + shardaddress);
                                        LIST_ContentsMerged.add(shardtype + "#" + shardaddress);
                                        NumContents.setText(String.valueOf(NUM_CONTENTS));
                                    }
                                }
                            });

                        } else if (shardtype.equals("AuctionContent")) {/*NUM_CONTENTS ++ */
                            firestoreManagerForUserProfile.readIgnore(shardaddress.split("/")[0], shardaddress.split("/")[1], new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    if(object != null) {
                                        NUM_CONTENTS++;
                                        LIST_Auctioncontents.add(shardtype + "#" + shardaddress);
                                        LIST_ContentsMerged.add(shardtype + "#" + shardaddress);
                                        NumContents.setText(String.valueOf(NUM_CONTENTS));
                                    }
                                }
                            });

                        } else if (shardtype.equals("Comment")) {
                            String[] addressSplit = shardaddress.split("/");
                            String subPath = "";
                            for (int i = 1; i < addressSplit.length; i++) {
                                subPath += addressSplit[i] + "/";
                            }
                            firestoreManagerForUserProfile.readIgnore(addressSplit[0], subPath, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    if(object != null) {
                                        NUM_COMMENTS++;
                                        LIST_Comments.add(shardtype + "#" + shardaddress);
                                        NumComments.setText(String.valueOf(NUM_COMMENTS));
                                    }
                                }
                            });
                        } else if (shardtype.equals("Like")){
                            firestoreManagerForUserProfile.readIgnore(shardaddress.split("/")[0], shardaddress.split("/")[1], new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    if(object != null) {
                                        NUM_LOVERS++;
                                        LIST_LOVERS.add(shardtype + "#" + shardaddress);
                                        NUMLovers.setText(String.valueOf(NUM_LOVERS));
                                        //recyclerView2.setAdapter(AdapterForLover);
                                    }
                                }
                            });
                        } else if (shardtype.equals("Earned")){
                            //,{"Type":"Earned","Address":"Content/asdf","Price":"1000"}
                            firestoreManagerForUserProfile.readIgnore(shardaddress.split("/")[0], shardaddress.split("/")[1], new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    if(object != null){
                                        String shardprice = shard.getAsJsonObject().get("Price").getAsString();
                                        NUM_EARNED = NUM_EARNED + Integer.parseInt(shardprice);
                                        LIST_Earned.add(shardtype + "#" + shardaddress + "#"+ shardprice);
                                        NumEarned.setText(String.valueOf(NUM_EARNED));
                                    }else{
                                        // NOTICE USER " UR EARNED DATA IS DELELTED FOR SOME REASON "
                                        String shardprice = shard.getAsJsonObject().get("Price").getAsString();
                                        NUM_EARNED = NUM_EARNED + Integer.parseInt(shardprice);
                                        LIST_Earned.add(shardtype + "#" + "DELETEDITEM_FLAG/DELETEDITEM_FLAG" + "#"+ shardprice);
                                        NumEarned.setText(String.valueOf(NUM_EARNED));
                                        // AND THEN JUST ENTER PRICE INFO
                                    }
                                }
                            });
                            // Extenstion needed for Earning Case...
                            // Like, if shardtye.eq("earned") , then readIgnore...
                            
                        }
                        else{
                        }

                    }
                    NumContents.setText(String.valueOf(NUM_CONTENTS));
                    NumComments.setText(String.valueOf(NUM_COMMENTS));
                    NUMLovers.setText(String.valueOf(NUM_LOVERS));
                    //"Address":"Content/MfnsDaivaiyedaOpTxdp/Comments/20220110163124295"}
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });
        storageManagerForUserProfile.downloadImg2View("UserProfileImage", userUID, userprofileimg, new Callback() {
            @Override
            public void OnCallback(Object object) {
            }});




        // BACKGROUND READ
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {

            }
        });

        EarnedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LIST_DATASET.equals(LIST_Earned)){
                    LIST_DATASET.clear();
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                    EarnedBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                }else{
                    ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    EarnedBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey1));
                    LoversBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    LIST_DATASET.clear();
                    LIST_DATASET.addAll(LIST_Earned);
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                }
            }
        });

        // CLICKLISTENER
        ContentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LIST_DATASET.equals(LIST_ContentsMerged)){
                    LIST_DATASET.clear();
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                    ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                }else{
                    ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey1));
                    CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    EarnedBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    LoversBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    LIST_DATASET.clear();
                    LIST_DATASET.addAll(LIST_ContentsMerged);
                    //AdapterForHistory.AddItem(LIST_Contents);
                    //Log.d("park",LIST_DATASET.toString());
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                }
            }
        });

        CommentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LIST_DATASET.equals(LIST_Comments)){
                    LIST_DATASET.clear();
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                    CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                }else{
                    ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    LoversBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey1));
                    EarnedBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    LIST_DATASET.clear();
                    LIST_DATASET.addAll(LIST_Comments);
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                }
            }
        });

        LoversBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LIST_DATASET.equals(LIST_LOVERS)){
                    LIST_DATASET.clear();
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                    LoversBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                }else{
                    LIST_DATASET.clear();
                    LIST_DATASET.addAll(LIST_LOVERS);
                    EarnedBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    LoversBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey1));
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                }
            }
        });

        resultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == RESULT_OK){
                            Intent intent = result.getData();

                            // 이미지뷰 씌우고
                            try{
                                Glide.with(UserhistoryActivity.this)
                                        .load(intent.getData())
                                        .into(userprofileimg);

                                //InputStream in = getContentResolver().openInputStream(intent.getData());
                                //Bitmap img = BitmapFactory.decodeStream(in);
                                //in.close();
                                //userprofileimg.setImageBitmap(img);

                                Bitmap img = ((BitmapDrawable)userprofileimg.getDrawable()).getBitmap();
                                storageManagerForUserProfile.upload("UserProfileImage" + "/" + user.getUid(), img, storageManagerForUserProfile.PROFILEIMG_THREADHOLD, new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        Log.d("why","asdf");
                                    }
                                });
                            }catch(Exception e){ }


                        }
                    }
                }
        );



    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    public void onBackPressed() {
        ActivityManager activityManager = (ActivityManager) getApplication().getSystemService( Activity.ACTIVITY_SERVICE );
        ActivityManager.RunningTaskInfo task = activityManager.getRunningTasks( 10 ).get(0);
        Log.d("TOPTOPTOP", task.toString());
        if(task.numActivities == 1){

            Dialog dialog = new Dialog(this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.exit_check_dialog);

            CardView yesBtn = dialog.findViewById(R.id.exitCheckDialogYesButton);
            yesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
            CardView noBtn = dialog.findViewById(R.id.exitCheckDialogNoButton);
            noBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();

        }else{
            finish();
        }
    }


}