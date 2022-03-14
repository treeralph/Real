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
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserhistoryActivity extends AppCompatActivity {

    int NUM_CONTENTS;
    int NUM_COMMENTS;
    int NUM_LOVERS;
    ActivityResultLauncher<Intent> resultLauncher;
    FirebaseStorage tempstorage;
    StorageReference tempref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userhistory_design);

        // XML VARIABLES
        ImageView userprofileimg = (ImageView) findViewById(R.id.UserhistoryProfileimg);
        ImageView setuserprofileimg = (ImageView) findViewById(R.id.UserhistoryActivitySetUserProfileImg);
        TextView userprofilenickname = (TextView) findViewById(R.id.UserhistoryUserNickname);
        ImageView setuserprofilenickname = (ImageView) findViewById(R.id.UserhistoryActivitySetUserProfileNickname);
        TextView NumContents = (TextView) findViewById(R.id.UserhistoryNumContents);
        TextView NumComments = (TextView) findViewById(R.id.UserhistoryNumComments);
        TextView NUMLovers = (TextView) findViewById(R.id.UserhistoryNumLovers);

        LinearLayout ContentsBtn = (LinearLayout) findViewById(R.id.UserhistoryBtnContents);
        LinearLayout AuctionContentsBtn = (LinearLayout) findViewById(R.id.UserhistoryBtnAuctionContents);
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
        List<String> LIST_Contents = new ArrayList<>();
        List<String> LIST_Auctioncontents = new ArrayList<>();
        List<String> LIST_Comments = new ArrayList<>();
        List<String> LIST_DATASET = new ArrayList<>();

        List<String> LIST_LOVERS = new ArrayList<>();


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
                                        NumContents.setText(String.valueOf(NUM_CONTENTS));
                                    }
                                }
                            });

                        } else if (shardtype.equals("AuctionContent")) {/*NUM_CONTENTS ++ */
                            firestoreManagerForUserProfile.readIgnore(shardaddress.split("/")[0], shardaddress.split("/")[1], new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    if(object != null) {
                                        LIST_Auctioncontents.add(shardtype + "#" + shardaddress);
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
                                        LIST_LOVERS.add(shardtype + "#" + shardaddress);
                                        //recyclerView2.setAdapter(AdapterForLover);
                                    }
                                }
                            });
                        }else{}

                    }
                    NumContents.setText(String.valueOf(NUM_CONTENTS));
                    NumComments.setText(String.valueOf(NUM_COMMENTS));
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


        // CLICKLISTENER
        ContentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LIST_DATASET.equals(LIST_Contents)){
                    LIST_DATASET.clear();
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                    ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                }else{
                    ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey1));
                    CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    AuctionContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    LIST_DATASET.clear();
                    LIST_DATASET.addAll(LIST_Contents);
                    //AdapterForHistory.AddItem(LIST_Contents);
                    //Log.d("park",LIST_DATASET.toString());
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                }
            }
        });

        AuctionContentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LIST_DATASET.equals(LIST_Auctioncontents)){
                    LIST_DATASET.clear();
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                    AuctionContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                }else{
                    ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    AuctionContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey1));
                    LIST_DATASET.clear();
                    LIST_DATASET.addAll(LIST_Auctioncontents);
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
                    CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey1));
                    AuctionContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                    LIST_DATASET.clear();
                    LIST_DATASET.addAll(LIST_Comments);
                    AdapterForHistory.notifyDataSetChanged();
                    recyclerView.setAdapter(AdapterForHistory);
                }
            }
        });

        setuserprofilenickname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomDialogForSetUserNickname dialog = new CustomDialogForSetUserNickname(UserhistoryActivity.this);
                dialog.setDialogListener(new CustomDialogForSetUserNickname.CustomDialogListener() {
                    @Override
                    public void onPositiveClicked(String Nickname) {
                        userprofilenickname.setText(Nickname);
                        firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "nickname", Nickname, new Callback() {
                            @Override
                            public void OnCallback(Object object) {

                            }
                        });
                    }

                    @Override
                    public void onNegativeClicked() {

                    }
                });
                dialog.show();


            }
        });
        setuserprofileimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra("CallType",0215);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                resultLauncher.launch(intent);
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
                                InputStream in = getContentResolver().openInputStream(intent.getData());
                                Bitmap img = BitmapFactory.decodeStream(in);
                                in.close();
                                userprofileimg.setImageBitmap(img);
                            }catch(Exception e){ }
                            // 스토리지에 업뎃하고
                            tempstorage = FirebaseStorage.getInstance();
                            tempref = tempstorage.getReference();
                            StorageReference tempcref = tempref.child("UserProfileImage/" + user.getUid());
                            InputStream in = null;
                            try {
                                in = getContentResolver().openInputStream(intent.getData());
                                Bitmap tempbit = BitmapFactory.decodeStream(in);
                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                tempbit.compress(Bitmap.CompressFormat.JPEG,100,baos);
                                byte[] data = baos.toByteArray();
                                tempcref.delete();
                                UploadTask uploadTask = tempcref.putBytes(data);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    }
                                });
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }


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