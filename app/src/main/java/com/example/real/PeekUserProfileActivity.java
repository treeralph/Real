package com.example.real;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.r0adkll.slidr.Slidr;
import com.r0adkll.slidr.model.SlidrConfig;
import com.r0adkll.slidr.model.SlidrInterface;
import com.r0adkll.slidr.model.SlidrPosition;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class PeekUserProfileActivity extends AppCompatActivity {

    ImageView userProfileImageView;
    ImageView userHierarchy;
    TextView userNickNameTextView;
    TextView userIncomeTextView;
    TextView userContentNumTextView;
    ImageView userReputationStar1;
    ImageView userReputationStar2;
    ImageView userReputationStar3;
    ImageView userReputationStar4;
    ImageView userReputationStar5;
    TextView userReputation;

    FirestoreManager firestoreManagerForUserProfile;
    StorageManager storageManagerForUserProfileImage;

    String userProfileUID;
    FirebaseUser currentUser;

    Bitmap userProfileImageBitmap;

    private SlidrInterface slidr;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_peek_user_profile);

        SlidrConfig config = new SlidrConfig.Builder()
                                .position(SlidrPosition.BOTTOM)
                                .scrimStartAlpha(0f)
                                .scrimEndAlpha(0f)
                                .build();
        slidr = Slidr.attach(this, config);


        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        userProfileImageView = findViewById(R.id.peekUserProfileActivityProfileImageImageView);
        userHierarchy = findViewById(R.id.peekUserProfileActivityUserHierarchy);
        userNickNameTextView = findViewById(R.id.peekUserProfileActivityProfileNickNameTextView);
        userIncomeTextView = findViewById(R.id.peekUserProfileActivityUserIncomeTextView);
        userContentNumTextView = findViewById(R.id.peekUserProfileActivityUserContentNumTextView);
        userReputationStar1 = findViewById(R.id.peekUserProfileActivityReputationStar1);
        userReputationStar2 = findViewById(R.id.peekUserProfileActivityReputationStar2);
        userReputationStar3 = findViewById(R.id.peekUserProfileActivityReputationStar3);
        userReputationStar4 = findViewById(R.id.peekUserProfileActivityReputationStar4);
        userReputationStar5 = findViewById(R.id.peekUserProfileActivityReputationStar5);
        userReputation = findViewById(R.id.peekUserProfileActivityReputationTextView);

        ArrayList<ImageView> starList = new ArrayList<>();
        starList.add(userReputationStar1);
        starList.add(userReputationStar2);
        starList.add(userReputationStar3);
        starList.add(userReputationStar4);
        starList.add(userReputationStar5);

        userProfileUID = getIntent().getStringExtra("userProfileUID"); // not current user UID

        // todo: Do not get bitmap through intent, Do get bitmap from server
        /*
        byte[] userProfileImageByteArrays = getIntent().getByteArrayExtra("userProfileImageByteArray");
        userProfileImageBitmap = BitmapFactory.decodeByteArray(userProfileImageByteArrays, 0, userProfileImageByteArrays.length);
        userProfileImageView.setImageBitmap(userProfileImageBitmap);

         */

        firestoreManagerForUserProfile = new FirestoreManager(this, "UserProfile", currentUser.getUid());
        storageManagerForUserProfileImage = new StorageManager(this, "UserProfileImage", currentUser.getUid());

        firestoreManagerForUserProfile.read("UserProfile", userProfileUID, new Callback() {
            @Override
            public void OnCallback(Object object) {
                UserProfile userProfile = (UserProfile) object;

                userNickNameTextView.setText(userProfile.getNickName());
                userIncomeTextView.setText(userProfile.getIncome());
                userContentNumTextView.setText(userProfile.getNumContent());

                String rating = userProfile.getRating();
                int numStar = Math.round(Float.valueOf(rating));
                for(int i=0; i<numStar; i++){
                    ImageView starImage = starList.get(i);
                    starImage.setImageResource(R.drawable.star_filled);
                }
                userReputation.setText(rating);

                storageManagerForUserProfileImage.downloadImg2View("UserProfileImage", userProfileUID, userProfileImageView, new Callback() {
                    @Override
                    public void OnCallback(Object object) {

                    }
                });
            }
        });

        TextView tempView = findViewById(R.id.peekUserProfileActivityEmptySpace);
        tempView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);

    }
}