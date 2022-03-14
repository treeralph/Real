package com.example.real;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.databasemanager.StorageManager;
import com.example.real.fragment.ImgViewFromGalleryFragmentForDetail;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class ImageViewActivity extends AppCompatActivity {

    ViewPager viewPager;
    ViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        StorageManager storageManager = new StorageManager(ImageViewActivity.this, "Image", user.getUid());

        String contentId = getIntent().getStringExtra("contentId");

        viewPager = (ViewPager)findViewById(R.id.ImageViewActivityViewPager);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference ref = storage.getReference();
        StorageReference sRef = ref.child("image/" + contentId + "/2000");
        sRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                for(StorageReference itemRef : listResult.getItems()){
                    ImgViewFromGalleryFragmentForDetail fragment = new ImgViewFromGalleryFragmentForDetail(itemRef);
                    adapter.addItem(fragment);
                    viewPager.setAdapter(adapter);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                e.printStackTrace();
            }
        });
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