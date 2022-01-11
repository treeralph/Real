package com.example.real;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
        setContentView(R.layout.activity_userhistory);

        // XML VARIABLES
        CircleImageView userprofileimg = (CircleImageView) findViewById(R.id.UserhistoryProfileimg);
        TextView userprofilenickname = (TextView) findViewById(R.id.UserhistoryUserNickname);
        TextView NumContents = (TextView) findViewById(R.id.UserhistoryNumContents);
        TextView NumComments = (TextView) findViewById(R.id.UserhistoryNumComments);

        Button ContentsBtn = (Button) findViewById(R.id.UserhistoryBtnContents);
        Button AuctionContentsBtn = (Button) findViewById(R.id.UserhistoryBtnAuctionContents);
        Button CommentsBtn = (Button) findViewById(R.id.UserhistoryBtnComments);

        TextView ToggleSection = (TextView) findViewById(R.id.UserhistoryToggleSection);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.UserhistoryRecyclerView);


        // GET CURRENT USER DATA INCLUDING USERLOG
        // On callback DISCRIMINATING USERLOG

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
                UserProfile CurrentUserProfile = (UserProfile)object;
                String nickname = CurrentUserProfile.getNickName();
                userprofilenickname.setText(nickname);
                String userlog = CurrentUserProfile.getUserLog();
                JsonParser parser = new JsonParser();
                Object tempparsed = parser.parse(userlog);
                JsonArray templog = (JsonArray) tempparsed;

                // DISCRIMINATING USERLOG
                int NUM_CONTENTS = 0; int NUM_COMMENTS = 0;
                for (JsonElement shard : templog){
                    String shardtype = shard.getAsJsonObject().get("Type").getAsString();
                    String shardaddress = shard.getAsJsonObject().get("Address").getAsString();
                    if (shardtype.equals("Content")){NUM_CONTENTS ++; LIST_Contents.add(shardtype + "#" + shardaddress);}
                    else if (shardtype.equals("AuctionContent")){NUM_CONTENTS ++; LIST_Auctioncontents.add(shardtype + "#" +shardaddress);}
                    else if (shardtype.equals("Comment")){NUM_COMMENTS ++; LIST_Comments.add(shardtype + "#" +shardaddress);}
                    else{}
                }
                NumContents.setText("싸지른 글 수 : "+ String.valueOf(NUM_CONTENTS));
                NumComments.setText("싸지른 댁글 수 : "+ String.valueOf(NUM_COMMENTS));
                //"Address":"Content/MfnsDaivaiyedaOpTxdp/Comments/20220110163124295"
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
                ToggleSection.setText(" 작성한 컨태ㄴ츠");
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
                ToggleSection.setText(" 작성한 옥션 컨텐츠");
                LIST_DATASET.clear();
                LIST_DATASET.addAll(LIST_Auctioncontents);
                AdapterForHistory.notifyDataSetChanged();
                recyclerView.setAdapter(AdapterForHistory);
            }
        });

        CommentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToggleSection.setText(" 작성한 댁글");
                LIST_DATASET.clear();
                LIST_DATASET.addAll(LIST_Comments);
                AdapterForHistory.notifyDataSetChanged();
                recyclerView.setAdapter(AdapterForHistory);
            }
        });



    }
}