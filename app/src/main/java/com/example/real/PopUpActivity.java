package com.example.real;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

public class PopUpActivity extends Activity {

    RelativeLayout relativeLayout;
    LinearLayout modifyBtn;
    LinearLayout deleteBtn;

    FirebaseUser currentUser;
    String currentUserUID;

    String contentUID;
    String contentID;
    String contentTime;

    FirestoreManager firestoreManagerForContent;
    FirestoreManager firestoreManagerForContents;
    StorageManager storageManagerForContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_for_content);

        relativeLayout = findViewById(R.id.popUpActivityRelativeLayout);
        modifyBtn = findViewById(R.id.popUpActivityModifyButton);
        deleteBtn = findViewById(R.id.popUpActivityDeleteButton);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserUID = currentUser.getUid();

        contentUID = getIntent().getStringExtra("ContentUID");
        contentID = getIntent().getStringExtra("ContentID");
        contentTime = getIntent().getStringExtra("ContentTime");

        firestoreManagerForContent = new FirestoreManager(PopUpActivity.this, "Content", currentUserUID);
        firestoreManagerForContents = new FirestoreManager(PopUpActivity.this, "Contents", currentUserUID);
        storageManagerForContent = new StorageManager(PopUpActivity.this, "Image", currentUserUID);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(contentUID.equals(currentUserUID)){


                } else{
                    Toast.makeText(PopUpActivity.this, "Permission deny", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(contentUID.equals(currentUserUID)){
                    try {
                        firestoreManagerForContents.delete("Contents", contentTime, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                firestoreManagerForContent.delete("Content", contentID, new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        storageManagerForContent.delete("image", contentID, new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {
                                                Log.w("ContentDelete", "Content was successfully deleted");
                                                Toast.makeText(PopUpActivity.this, "Content was successfully deleted", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    } catch(Exception e){
                        Log.e("ContentDelete", e.getMessage());
                    }
                } else{
                    Toast.makeText(PopUpActivity.this, "Permission deny", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}