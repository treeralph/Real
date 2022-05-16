package com.example.real;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.real.data.AuctionContent;
import com.example.real.data.Content;
import com.example.real.data.Contents;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class ReputationActivity extends AppCompatActivity {

    private String TAG = "ReputationActivity";

    FirebaseUser user;
    String anotherUserUID;

    String contentId;

    ImageView contentImageView;
    TextView contentTitleTextView;
    TextView contentPriceTextView;
    TextView contentUIDTextView;
    ImageView reputationUserProfileImageView;
    TextView reputationUIDTextView;
    CardView checkButton;

    LinearLayout reputation_1;
    LinearLayout reputation_2;
    LinearLayout reputation_3;
    LinearLayout reputation_4;
    LinearLayout reputation_5;
    ArrayList<LinearLayout> reputationList;

    ImageView star_1;
    ImageView star_2;
    ImageView star_3;
    ImageView star_4;
    ImageView star_5;
    ArrayList<ImageView> galaxy;

    FirestoreManager firestoreManagerForContents;
    FirestoreManager firestoreManagerForContent;
    FirestoreManager firestoreManagerForAuctionContent;
    FirestoreManager firestoreManagerForUserProfile;
    StorageManager storageManagerForContent;
    StorageManager storageManagerForUserProfile;

    String contentUID;

    int reputationScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reputation);

        contentId = getIntent().getStringExtra("contentId");
        String tUser_1 = getIntent().getStringExtra("user1");
        String tUser_2 = getIntent().getStringExtra("user2");
        if(tUser_1.equals(user.getUid())){
            anotherUserUID = tUser_2;
        }else{
            anotherUserUID = tUser_1;
        }

        user = FirebaseAuth.getInstance().getCurrentUser();

        contentImageView = findViewById(R.id.reputationActivityContentImageImageView);
        contentTitleTextView = findViewById(R.id.reputationActivityContentTitleTextView);
        contentPriceTextView = findViewById(R.id.reputationActivityContentPriceTextView);
        contentUIDTextView = findViewById(R.id.reputationActivityContentUIDTextView);
        reputationUserProfileImageView = findViewById(R.id.reputationActivityUserProfileImageView);
        reputationUIDTextView = findViewById(R.id.reputationActivityUserProfileUIDTextView);
        checkButton = findViewById(R.id.reputationActivityReputationCheckButton);

        reputation_1 = findViewById(R.id.reputationActivityReputation_1);
        reputation_2 = findViewById(R.id.reputationActivityReputation_2);
        reputation_3 = findViewById(R.id.reputationActivityReputation_3);
        reputation_4 = findViewById(R.id.reputationActivityReputation_4);
        reputation_5 = findViewById(R.id.reputationActivityReputation_5);

        star_1 = findViewById(R.id.reputationActivityStar_1);
        star_2 = findViewById(R.id.reputationActivityStar_2);
        star_3 = findViewById(R.id.reputationActivityStar_3);
        star_4 = findViewById(R.id.reputationActivityStar_4);
        star_5 = findViewById(R.id.reputationActivityStar_5);


        firestoreManagerForContents = new FirestoreManager(this, "Contents", user.getUid());
        firestoreManagerForContent = new FirestoreManager(this, "Content", user.getUid());
        firestoreManagerForAuctionContent = new FirestoreManager(this, "AuctionContent", user.getUid());
        firestoreManagerForUserProfile = new FirestoreManager(this, "UserProfile", user.getUid());
        storageManagerForContent = new StorageManager(this, "image", user.getUid());
        storageManagerForUserProfile = new StorageManager(this, "UserProfileImage", user.getUid());

        storageManagerForUserProfile.downloadImg2View("UserProfileImage", anotherUserUID, reputationUserProfileImageView, new Callback() {
            @Override
            public void OnCallback(Object object) {
                String contentImagePath = "image/" + contentId + "/100";
                storageManagerForContent.downloadImg2View(contentImagePath, "0", contentImageView, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        firestoreManagerForUserProfile.read("UserProfile", anotherUserUID, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                UserProfile userProfile = (UserProfile) object;
                                String nickname = userProfile.getNickName();
                                contentUIDTextView.setText(nickname);
                                reputationUIDTextView.setText(nickname);
                                firestoreManagerForContents.read("Contents", contentId, new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        Contents contents = (Contents) object;
                                        switch (contents.getContentType()){
                                            case "Content":
                                                firestoreManagerForContent.read("Content", contentId, new Callback() {
                                                    @Override
                                                    public void OnCallback(Object object) {
                                                        Content content = (Content) object;
                                                        contentUID = content.getUid();
                                                        contentTitleTextView.setText(content.getTitle());
                                                        contentPriceTextView.setText(content.getPrice());
                                                    }
                                                });
                                                break;
                                            case "AuctionContent":
                                                firestoreManagerForAuctionContent.read("Content", contentId, new Callback() {
                                                    @Override
                                                    public void OnCallback(Object object) {
                                                        AuctionContent auctionContent = (AuctionContent) object;
                                                        contentUID = auctionContent.getUid();
                                                        contentTitleTextView.setText(auctionContent.getTitle());
                                                        contentPriceTextView.setText(auctionContent.getPrice());
                                                    }
                                                });
                                                break;
                                            default:
                                                break;
                                        }
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });

        reputationList = new ArrayList<>();
        reputationList.add(reputation_1);
        reputationList.add(reputation_2);
        reputationList.add(reputation_3);
        reputationList.add(reputation_4);
        reputationList.add(reputation_5);

        galaxy = new ArrayList<>();
        galaxy.add(star_1);
        galaxy.add(star_2);
        galaxy.add(star_3);
        galaxy.add(star_4);
        galaxy.add(star_5);

        for(int i=0; i<reputationList.size(); i++){
            int index = i + 1;
            LinearLayout temp = reputationList.get(i);
            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    reputationScore = index;
                    for(int j=0; j<reputationScore; j++){
                        galaxy.get(j).setImageResource(R.drawable.star_filled);
                    }
                }
            });
        }

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String type;
                String address =anotherUserUID + "/" + contentId + "/" + contentPriceTextView.getText();
                if(user.getUid().equals(contentUID)){
                    type = "sellHistory";
                }else{
                    type = "buyHistory";
                }
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("Type", type);
                jsonObject.addProperty("Address", address);
                firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        UserProfile userProfile = (UserProfile) object;
                        String rating = userProfile.getRating();
                        String userLog = userProfile.getUserLog();
                        JsonParser parser = new JsonParser();
                        Object tempParsed = parser.parse(userLog);
                        JsonArray tempLog = (JsonArray) tempParsed;
                        tempLog.add(jsonObject);
                        firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "UserLog", tempLog.toString(), new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                // 점수 반영
                                String newUserProfileRating = "";
                                if(rating.equals("0")){
                                    newUserProfileRating = String.valueOf(reputationScore) + "/1";
                                }else{
                                    String[] ratingSplit = rating.split("/");
                                    double oldRating = Double.valueOf(ratingSplit[0]);
                                    int ratingTime = Integer.parseInt(ratingSplit[1]);
                                    double newRating = ((oldRating * ratingTime) + reputationScore) / (ratingTime + 1);
                                    newUserProfileRating = String.valueOf(newRating) + "/" + String.valueOf(ratingTime + 1);
                                }
                                firestoreManagerForUserProfile.update("UserProfile", anotherUserUID, "rating", newUserProfileRating, new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });







    }
}