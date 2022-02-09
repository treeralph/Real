package com.example.real;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.real.adapter.ExpandableListAdapter;
import com.example.real.adapter.RecyclerViewAdapterForHistory;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserhistoryActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userhistory_design);

        // XML VARIABLES
        ImageView userprofileimg = (ImageView) findViewById(R.id.UserhistoryProfileimg);
        TextView userprofilenickname = (TextView) findViewById(R.id.UserhistoryUserNickname);
        TextView NumContents = (TextView) findViewById(R.id.UserhistoryNumContents);
        TextView NumComments = (TextView) findViewById(R.id.UserhistoryNumComments);

        LinearLayout ContentsBtn = (LinearLayout) findViewById(R.id.UserhistoryBtnContents);
        LinearLayout AuctionContentsBtn = (LinearLayout) findViewById(R.id.UserhistoryBtnAuctionContents);
        LinearLayout CommentsBtn = (LinearLayout) findViewById(R.id.UserhistoryBtnComments);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.UserhistoryRecyclerViewDesign);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

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


        RecyclerViewAdapterForHistory AdapterForHistory = new RecyclerViewAdapterForHistory(LIST_DATASET, UserhistoryActivity.this);
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
                    int NUM_CONTENTS = 0;
                    int NUM_COMMENTS = 0;
                    for (JsonElement shard : templog) {
                        String shardtype = shard.getAsJsonObject().get("Type").getAsString();
                        String shardaddress = shard.getAsJsonObject().get("Address").getAsString();
                        if (shardtype.equals("Content")) {
                            NUM_CONTENTS++;
                            LIST_Contents.add(shardtype + "#" + shardaddress);
                        } else if (shardtype.equals("AuctionContent")) {/*NUM_CONTENTS ++ */
                            ;
                            LIST_Auctioncontents.add(shardtype + "#" + shardaddress);
                        } else if (shardtype.equals("Comment")) {
                            NUM_COMMENTS++;
                            LIST_Comments.add(shardtype + "#" + shardaddress);
                        } else {
                        }
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
                ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey1));
                CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                AuctionContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                LIST_DATASET.clear();
                //LIST_DATASET.addAll(LIST_Contents);
                AdapterForHistory.AddItem(LIST_Contents);
                Log.d("park",LIST_DATASET.toString());
                //AdapterForHistory.notifyDataSetChanged();
                recyclerView.setAdapter(AdapterForHistory);
            }
        });

        AuctionContentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                AuctionContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey1));
                LIST_DATASET.clear();
                LIST_DATASET.addAll(LIST_Auctioncontents);
                AdapterForHistory.notifyDataSetChanged();
                recyclerView.setAdapter(AdapterForHistory);
            }
        });

        CommentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                CommentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.grey1));
                AuctionContentsBtn.setBackgroundColor(getApplicationContext().getResources().getColor(R.color.white));
                LIST_DATASET.clear();
                LIST_DATASET.addAll(LIST_Comments);
                AdapterForHistory.notifyDataSetChanged();
                recyclerView.setAdapter(AdapterForHistory);
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }
}