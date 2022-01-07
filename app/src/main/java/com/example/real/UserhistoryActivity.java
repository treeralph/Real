package com.example.real;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
                String userlog = CurrentUserProfile.getUserLog();
                userprofilenickname.setText(nickname);}});
        storageManagerForUserProfile.downloadImg2View("UserProfileImage", userUID, userprofileimg, new Callback() {
            @Override
            public void OnCallback(Object object) {
            }});


        // BACKGROUND READ
        int NUM_CONTENTS ; int NUM_COMMENTS;
        Thread thread = new Thread();

        // CLICKLISTENER
        ContentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // APPEND DATA ON DATASET
                // ADAPTER.SET DATASET
            }
        });

        AuctionContentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // APPEND DATA ON DATASET
                // ADAPTER.SET DATASET
            }
        });

        CommentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // APPEND DATA ON DATASET
                // ADAPTER.SET DATASET
            }
        });



    }
}