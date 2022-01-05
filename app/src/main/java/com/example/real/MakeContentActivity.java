package com.example.real;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.data.Content;
import com.example.real.data.Contents;
import com.example.real.data.LoadingHandler;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.fragment.ImgViewFromGalleryFragment;
import com.example.real.tool.NumberingMachine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.InputStream;

public class MakeContentActivity extends AppCompatActivity {

    Button MakeContentBtn;
    Button ChooseImgBtn;
    EditText editTextTitle;
    EditText editTextContent;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    FirebaseUser user;
    NumberingMachine numberingMachine;

    Thread thread;
    private final int IMGSELECTINTENTREQUESTCODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_content);

        numberingMachine = new NumberingMachine();
        user = FirebaseAuth.getInstance().getCurrentUser();

        FirestoreManager firestoreManagerForContent = new FirestoreManager(MakeContentActivity.this, "Content", user.getUid());
        FirestoreManager firestoreManagerForContents = new FirestoreManager(MakeContentActivity.this, "Contents", user.getUid());
        StorageManager storageManager = new StorageManager(MakeContentActivity.this, "Image", user.getUid());

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        MakeContentBtn = findViewById(R.id.makeContentMakeBtn);
        ChooseImgBtn = findViewById(R.id.makeContentImgSelectBtn);
        editTextTitle = findViewById(R.id.makeContentEditTextTitle);
        editTextContent = findViewById(R.id.makeContentEditTextContent);
        viewPager = findViewById(R.id.makeContentViewPager);

        ChooseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMGSELECTINTENTREQUESTCODE);
            }
        });

        MakeContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        MakeContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Content content = new Content(editTextTitle.getText().toString(), editTextContent.getText().toString(), user.getUid());
                        firestoreManagerForContent.write(content, "Content", new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                String contentId = (String)object;
                                String contentType = content.getContentType();
                                Contents contents = new Contents(contentId, contentType);
                                firestoreManagerForContents.write(contents, "Contents", content.getTime(), new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        // image
                                        for(int i=0; i<adapter.getCount(); i++){
                                            int j = i;
                                            numberingMachine.add();
                                            ImgViewFromGalleryFragment TempFregment = (ImgViewFromGalleryFragment)adapter.getItem(i);
                                            Bitmap TempImg = TempFregment.getBitmap();
                                            storageManager.upload("image/" + contentId + "/2000/" + String.valueOf(j), TempImg, 2000, new Callback() {
                                                @Override
                                                public void OnCallback(Object object) {
                                                    storageManager.upload("image/" + contentId + "/1000/" + String.valueOf(j), TempImg, 1000, new Callback() {
                                                        @Override
                                                        public void OnCallback(Object object) {
                                                            storageManager.upload("image/" + contentId + "/100/" + String.valueOf(j), TempImg, 100, new Callback() {
                                                                @Override
                                                                public void OnCallback(Object object) {
                                                                    if(numberingMachine.getNumber() == adapter.getCount()){
                                                                        Message message = Message.obtain();
                                                                        message.obj = "씨발";
                                                                        LoadingActivity.LoadingHandler handler = ((LoadingActivity)LoadingActivity.LoadingContext).handler;
                                                                        handler.sendMessage(message);
                                                                        //Intent intent = new Intent(MakeContentActivity.this, ContentsActivity.class);
                                                                        //startActivity(intent);
                                                                        //finish();
                                                                    }
                                                                }
                                                            });
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
                });
                thread.start();
                Intent intent = new Intent(MakeContentActivity.this, LoadingActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                finish();
            }
        });







    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMGSELECTINTENTREQUESTCODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    ImgViewFromGalleryFragment fragment = new ImgViewFromGalleryFragment(img);
                    adapter.addItem(fragment);
                    viewPager.setAdapter(adapter);
                } catch (Exception e) {
                }
            }
        }
    }
}