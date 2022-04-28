package com.example.real;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.data.ExifOrientationStream;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.fragment.ImgViewFromGalleryFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

public class SetUserProfileActivity extends AppCompatActivity {

    private final int IMGSELECTINTENTREQUESTCODE = 0;
    FirebaseUser user;

    EditText setuserprofile2nickname;
    ImageView setuserprofile2userprofileimage;
    TextView setuserprofile2registertime;
    EditText setuserprofile2description;
    Button setuserprofile2setbutton;
    ImageView getSetuserprofile2paddlebtn;

    FirebaseStorage tempstorage;
    StorageReference tempref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_user_profile_design);

        user = FirebaseAuth.getInstance().getCurrentUser();

        setuserprofile2nickname = findViewById(R.id.setuserprofile2nickname);
        setuserprofile2userprofileimage = findViewById(R.id.setuserprofile2userprofileimage);
        setuserprofile2registertime = findViewById(R.id.setuserprofile2registertime);
        setuserprofile2setbutton = findViewById(R.id.setuserprofile2setbutton);
        setuserprofile2description = findViewById(R.id.setuserprofile2userdescription);
        getSetuserprofile2paddlebtn = findViewById(R.id.setuserprofile2paddle);


        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                SetUserProfileActivity.this, "UserProfile", user.getUid());
        StorageManager storageManagerForUserProfile = new StorageManager(
                SetUserProfileActivity.this, "UserProfileImage", user.getUid());

        // 유저 정보 읽어옴
        firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
            @Override
            public void OnCallback(Object object) {
                UserProfile tempUserProfile = (UserProfile)object;

                String nickname = tempUserProfile.getNickName();
                String registertime = tempUserProfile.getRegisterTime();
                String description = tempUserProfile.getDescription();
                storageManagerForUserProfile.download("UserProfileImage", user.getUid(), new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        Bitmap userProfileBitmap = (Bitmap)object;
                        //ContentUserProfileImgImageView.setImageBitmap(userProfileBitmap);
                        setuserprofile2userprofileimage.setImageBitmap(userProfileBitmap);
                    }
                });
                setuserprofile2nickname.setText(nickname);
                setuserprofile2registertime.setText(registertime);
                setuserprofile2description.setText(description);

            }
        });

        // 이미지 변경
        setuserprofile2userprofileimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Trigger Getting Image Event from user device
                // Then, set image on imageview
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMGSELECTINTENTREQUESTCODE);
            }
        });

        // 변경사항 업로드
        setuserprofile2setbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Upload Current EditText,Image
                // Then, Intent to Contents Activity

                String tempNickName = setuserprofile2nickname.getText().toString();
                /////////////1211

                String tempDescription = setuserprofile2description.getText().toString();
                FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(SetUserProfileActivity.this,"UserProfile",user.getUid());
                firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "description", tempDescription, new Callback() {
                    @Override
                    public void OnCallback(Object object) {

                    }
                });
                firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "nickname", tempNickName, new Callback() {
                    @Override
                    public void OnCallback(Object object) {

                        setuserprofile2userprofileimage.setDrawingCacheEnabled(true);
                        setuserprofile2userprofileimage.buildDrawingCache();
                        Bitmap tempbit = ((BitmapDrawable)setuserprofile2userprofileimage.getDrawable()).getBitmap();


                        storageManagerForUserProfile.upload("UserProfileImage" + "/" + user.getUid(), tempbit, storageManagerForUserProfile.PROFILEIMG_THREADHOLD, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                Intent ToContents = new Intent(SetUserProfileActivity.this,ContentsActivity.class);
                                startActivity(ToContents);
                                finish();
                            }
                        });

                    }
                });
            }
        });

        getSetuserprofile2paddlebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SetUserProfileActivity.this, PaddleCustomActivity.class);
                startActivity(intent);
            }
        });

    }





    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // uri = data.getData()
        if(requestCode==IMGSELECTINTENTREQUESTCODE){
            if(resultCode==RESULT_OK){
                try{
                    Glide.with(SetUserProfileActivity.this)
                            .load(data.getData())
                            .into(setuserprofile2userprofileimage);


                    //InputStream in = getContentResolver().openInputStream(data.getData());
                    //Bitmap img = BitmapFactory.decodeStream(in);
                    //in.close();
                    //setuserprofile2userprofileimage.setImageBitmap(img);
                }catch(Exception e){

                }
            }
        }
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