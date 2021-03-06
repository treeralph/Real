package com.example.real;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.data.Content;
import com.example.real.data.Contents;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.fragment.ImgViewFromGalleryFragment;
import com.example.real.tool.CategoryDialog;
import com.example.real.tool.NumberingMachine;
import com.example.real.tool.SearchTool;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.naver.maps.geometry.LatLng;

import java.io.InputStream;
import java.util.ArrayList;

public class MakeContentActivity extends AppCompatActivity {

    private final int LocationActivityRequestCode = 8;

    CardView MakeContentBtn;
    FrameLayout ChooseImgBtn;
    EditText editTextTitle;
    EditText editTextContent;
    EditText editTextPrice;
    ViewPager viewPager;
    ViewPagerAdapter adapter;
    FirebaseUser user;
    NumberingMachine numberingMachine;
    TextView categoryText;
    TextView locationText;

    Thread thread;
    private final int IMGSELECTINTENTREQUESTCODE = 0;

    String latLng;
    String adm_cd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_content_design);

        numberingMachine = new NumberingMachine();
        user = FirebaseAuth.getInstance().getCurrentUser();

        FirestoreManager firestoreManagerForContent = new FirestoreManager(MakeContentActivity.this, "Content", user.getUid());
        FirestoreManager firestoreManagerForContents = new FirestoreManager(MakeContentActivity.this, "Contents", user.getUid());
        StorageManager storageManager = new StorageManager(MakeContentActivity.this, "Image", user.getUid());

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        MakeContentBtn = findViewById(R.id.makeContentMakeButtonDesign);
        ChooseImgBtn = findViewById(R.id.makeContentImageSelectButtonDesign);
        editTextTitle = findViewById(R.id.makeContentTitleEditTextDesign);
        editTextContent = findViewById(R.id.makeContentDescriptionEditTextDesign);
        editTextPrice = findViewById(R.id.makeContentPriceEditTextDesign);
        categoryText = findViewById(R.id.makeContentCategoryTextView);
        locationText = findViewById(R.id.makeContentLocationTextView);
        viewPager = findViewById(R.id.makeContentViewPagerDesign);

        locationText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // request code = 8;
                Intent intent = new Intent(MakeContentActivity.this, LocationActivity.class);
                startActivityForResult(intent, LocationActivityRequestCode);
            }
        });

        categoryText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CategoryDialog categoryDialog = new CategoryDialog(MakeContentActivity.this, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        String category = (String) object;
                        categoryText.setText(category);
                    }
                });
                categoryDialog.setCanceledOnTouchOutside(true);
                categoryDialog.setCancelable(true);
                categoryDialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                categoryDialog.show();
            }
        });

        ChooseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, IMGSELECTINTENTREQUESTCODE);
            }
        });

        MakeContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editTextTitle.getText().toString().equals("")){
                    Toast.makeText(MakeContentActivity.this, "????????? ????????? ?????????", Toast.LENGTH_SHORT).show(); }
                else if(editTextContent.getText().toString().equals("")){
                    Toast.makeText(MakeContentActivity.this, "????????? ????????? ?????????", Toast.LENGTH_SHORT).show(); }
                else if(adapter.getCount()==0){
                    Toast.makeText(MakeContentActivity.this, "???????????? ????????? ????????????", Toast.LENGTH_SHORT).show(); }
                else if(editTextPrice.getText().toString().equals("")){
                    Toast.makeText(MakeContentActivity.this, "????????? ????????? ?????????", Toast.LENGTH_SHORT).show(); }
                else if(categoryText.getText().toString().equals("")){
                    Toast.makeText(MakeContentActivity.this, "??????????????? ????????? ?????????", Toast.LENGTH_SHORT).show(); }
                else if(locationText.getText().toString().equals("")){
                    Toast.makeText(MakeContentActivity.this, "????????? ????????? ?????????", Toast.LENGTH_SHORT).show(); }
                else{
                    thread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            Content content = new Content(editTextTitle.getText().toString(), editTextContent.getText().toString(), user.getUid(), categoryText.getText().toString(), locationText.getText().toString(), editTextPrice.getText().toString(), latLng, adm_cd);
                            firestoreManagerForContent.write(content, "Content", new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    String contentId = (String)object;
                                    String contentType = content.getContentType();
                                    String contentTitle = content.getTitle();
                                    SearchTool searchTool = new SearchTool();
                                    ArrayList<String> stringList = new ArrayList<>();
                                    for(String s: contentTitle.split(" ")){
                                        stringList.add(s);
                                    }
                                    Contents contents = new Contents(contentId, contentType, contentTitle, categoryText.getText().toString(), searchTool.makeCase(stringList), latLng, content.getTime(), adm_cd, "ACQUIRED");
                                    firestoreManagerForContents.write(contents, "Contents", contentId, new Callback() {
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
                                                                            message.obj = "ContentMakingDone";
                                                                            LoadingActivity.LoadingHandler handler = ((LoadingActivity)LoadingActivity.LoadingContext).handler;
                                                                            handler.sendMessage(message);

                                                                            //UPLOAD USERLOG
                                                                            FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                                                                                    MakeContentActivity.this, "UserProfile", user.getUid());

                                                                            firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
                                                                                @Override
                                                                                public void OnCallback(Object object) {
                                                                                    UserProfile userprofile = (UserProfile) object;
                                                                                    String userlog = userprofile.getUserLog();
                                                                                    String address = "Content/" + contentId;
                                                                                    if(userlog.equals("")){
                                                                                        // Create Json Obj & JsonArray
                                                                                        JsonArray frame = new JsonArray();
                                                                                        JsonObject init = new JsonObject();
                                                                                        init.addProperty("Type","Content");
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
                                                                                        temp.addProperty("Type", "Content");
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
        }else if(requestCode == LocationActivityRequestCode){
            if(resultCode == RESULT_OK){
                try{
                    String location = data.getStringExtra("Location");
                    latLng = data.getStringExtra("LatLng");
                    adm_cd = data.getStringExtra("Adm_cd");
                    locationText.setText(location);


                } catch(Exception e){

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