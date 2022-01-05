package com.example.real;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.firebase.ui.auth.data.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;
import java.util.ArrayList;

public class UserProfileActivity extends AppCompatActivity {

    private final int IMGSELECTINTENTREQUESTCODE = 0;

    ImageView imageView;
    EditText editText;
    TextView textView;
    Button imgSelectBtn;
    Button checkBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        imageView = (ImageView)findViewById(R.id.UserProfileProfileImageView);
        editText = (EditText)findViewById(R.id.UserProfileNickNameEditText);
        textView = (TextView)findViewById(R.id.UserProfileCommunicationTextView);
        imgSelectBtn = (Button)findViewById(R.id.UserProfileProfileImageSelectBtn);
        checkBtn = (Button)findViewById(R.id.UserProfileCheckBtn);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        editText.setText(user.getUid());

        imgSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMGSELECTINTENTREQUESTCODE);
            }
        });

        // UserProfileActivity에 기본 이미지를 삽입해 놓자.

        checkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tempNickName = editText.getText().toString();

                FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(UserProfileActivity.this, "UserProfile", user.getUid());
                firestoreManagerForUserProfile.search("UserProfile", "nickname", tempNickName, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        try {
                            ArrayList<UserProfile> UserProfileList = (ArrayList<UserProfile>) object;
                            UserProfile errorCheck = UserProfileList.get(0);
                            textView.setText("The NickName Already exist. Try new NickName.");

                        } catch(Exception e){
                            UserProfile userProfile = new UserProfile("0", tempNickName);
                            firestoreManagerForUserProfile.write(userProfile, "UserProfile", user.getUid(), new Callback() {
                                @Override
                                public void OnCallback(Object object) {

                                    imageView.setDrawingCacheEnabled(true);
                                    imageView.buildDrawingCache();
                                    Bitmap bitmapImage = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                                    StorageManager storageManager = new StorageManager(UserProfileActivity.this, "UserProfile", user.getUid());
                                    storageManager.upload("UserProfileImage" + "/" + user.getUid(), bitmapImage,50, new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {
                                            String check = (String)object;
                                            if(check.equals("0")){
                                                Intent intent = new Intent(UserProfileActivity.this, ContentsActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else{
                                                // upload failure
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==IMGSELECTINTENTREQUESTCODE){
            if(resultCode==RESULT_OK){
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    imageView.setImageBitmap(img);

                }catch(Exception e){

                }
            }
        }
    }
}