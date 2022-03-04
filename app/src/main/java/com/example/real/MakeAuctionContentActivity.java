package com.example.real;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.data.AuctionContent;
import com.example.real.data.Content;
import com.example.real.data.Contents;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.RealTimeDatabaseManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.fragment.ImgViewFromGalleryFragment;
import com.example.real.tool.NumberingMachine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStream;

public class MakeAuctionContentActivity extends AppCompatActivity {

    Button MakeContentBtn;
    Button ChooseImgBtn;
    EditText editTextTitle;
    EditText editTextContent;
    EditText editTextAuctionPrice;
    EditText editTextAuctionDuration;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    FirebaseUser user;
    NumberingMachine numberingMachine;

    private final int IMGSELECTINTENTREQUESTCODE = 0;

    Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_auction_content);

        numberingMachine = new NumberingMachine();
        user = FirebaseAuth.getInstance().getCurrentUser();

        FirestoreManager firestoreManagerForContent = new FirestoreManager(MakeAuctionContentActivity.this, "AuctionContent", user.getUid());
        FirestoreManager firestoreManagerForContents = new FirestoreManager(MakeAuctionContentActivity.this, "Contents", user.getUid());
        RealTimeDatabaseManager realTimeDatabaseManagerForAuctionContent = new RealTimeDatabaseManager(MakeAuctionContentActivity.this, "MutexLock", user.getUid());
        StorageManager storageManager = new StorageManager(MakeAuctionContentActivity.this, "Image", user.getUid());

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        MakeContentBtn = findViewById(R.id.makeAuctionContentMakeBtn);
        ChooseImgBtn = findViewById(R.id.makeAuctionContentImgSelectBtn);
        editTextTitle = findViewById(R.id.makeAuctionContentEditTextTitle);
        editTextContent = findViewById(R.id.makeAuctionContentEditTextContent);
        editTextAuctionPrice = findViewById(R.id.makeAuctionContentEditTextAuctionPrice);
        editTextAuctionDuration = findViewById(R.id.makeAuctionContentSelectDeadLine);
        viewPager = findViewById(R.id.makeAuctionContentViewPager);

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
                /***************/
                AuctionContent auctionContent = new AuctionContent(
                        editTextTitle.getText().toString(),
                        editTextContent.getText().toString(),
                        user.getUid(),
                        editTextAuctionPrice.getText().toString(),
                        editTextAuctionDuration.getText().toString());

                // If u want catch exception, write on upper side of thread.
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        firestoreManagerForContent.write(auctionContent, "Content", new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                String contentId = (String)object;
                                String contentType = auctionContent.getContentType(); //
                                Contents contents = new Contents(contentId, contentType);
                                firestoreManagerForContents.write(contents, "Contents", auctionContent.getTime(), new Callback() {
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
                                                                        message.obj = "AuctionContentMakingDone";
                                                                        LoadingActivity.LoadingHandler handler = ((LoadingActivity)LoadingActivity.LoadingContext).handler;
                                                                        handler.sendMessage(message);

                                                                        //UPLOAD USERLOG
                                                                        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                                                                                MakeAuctionContentActivity.this, "UserProfile", user.getUid());

                                                                        firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
                                                                            @Override
                                                                            public void OnCallback(Object object) {
                                                                                realTimeDatabaseManagerForAuctionContent.writeMutex(contentId);

                                                                                UserProfile userprofile = (UserProfile) object;
                                                                                String userlog = userprofile.getUserLog();
                                                                                String address = "Content/" + contentId;
                                                                                if(userlog.equals("")){
                                                                                    // Create Json Obj & JsonArray
                                                                                    JsonArray frame = new JsonArray();
                                                                                    JsonObject init = new JsonObject();
                                                                                    init.addProperty("Type","AuctionContent");
                                                                                    init.addProperty("Address",address);
                                                                                    frame.add(init);
                                                                                    firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "UserLog",
                                                                                            frame.toString(), new Callback() {
                                                                                                @Override
                                                                                                public void OnCallback(Object object) {

                                                                                                }
                                                                                            });
                                                                                } else{
                                                                                    // Parsing JsonArray
                                                                                    JsonParser parser = new JsonParser();
                                                                                    Object tempparsed = parser.parse(userlog);
                                                                                    JsonArray templog = (JsonArray) tempparsed;

                                                                                    // Create Json Obj
                                                                                    JsonObject temp = new JsonObject();
                                                                                    temp.addProperty("Type", "AuctionContent");
                                                                                    temp.addProperty("Address",address);

                                                                                    // Add & Update
                                                                                    templog.add(temp);
                                                                                    firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "UserLog",
                                                                                            templog.toString(), new Callback() {
                                                                                                @Override
                                                                                                public void OnCallback(Object object) {

                                                                                                }
                                                                                            });
                                                                                }
                                                                            }
                                                                        });
                                                                    }
                                                                }
                                                            });
                                                        }
                                                    });
                                                }
                                            });
                                    /*
                                    storageManager.upload("image/" + contentId + "/" + String.valueOf(i), TempImg, new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {
                                            if(numberingMachine.getNumber() == adapter.getCount()){
                                                Intent intent = new Intent(MakeAuctionContentActivity.this, ContentsActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });

                                     */
                                        }
                                    }
                                });
                            }
                        });
                    }
                });

                thread.start();
                Intent intent = new Intent(MakeAuctionContentActivity.this, LoadingActivity.class);
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