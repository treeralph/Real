package com.example.real;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.real.adapter.RecyclerViewAdapterForContents;
import com.example.real.data.Contents;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.tool.OnSwipeTouchListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;

public class ContentsActivity extends AppCompatActivity {

    FirebaseUser user;
    LinearLayout chatRoomBtn;
    RecyclerView recyclerView;
    LinearLayout makeContentBtn;
    LinearLayout userHistoryBtn;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_contents);
        setContentView(R.layout.activity_contents_design);

        recyclerView = findViewById(R.id.ContentsActivityRecyclerViewDesign);
        makeContentBtn = findViewById(R.id.ContentsMakeContentBtnDesign);
        user = FirebaseAuth.getInstance().getCurrentUser();
        chatRoomBtn = findViewById(R.id.ContentsActivityChatRoomBtnDesign);
        userHistoryBtn = findViewById(R.id.ContentsActivityUserHistoryButton);
        swipeRefreshLayout = findViewById(R.id.ContentsActivitySwipeRefreshLayout);

        /*
        recyclerView = findViewById(R.id.minetestRecyclerView);
        makeContentBtn = findViewById(R.id.ContentsMakeContentBtn);
        SettingPopupImage = findViewById(R.id.ContentsSettingPopupImg);
        user = FirebaseAuth.getInstance().getCurrentUser();
        chatRoomBtn = findViewById(R.id.ContentsActivityChatRoomBtn);
         */




        userHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentsActivity.this, UserhistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        chatRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentsActivity.this, ChattingRoomActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });


        // Device Token Renewing
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("FIREBASEMESSAGING", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();
                        Log.d("FIREBASEMESSAGING", token);

                        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(ContentsActivity.this, "UserProfile", user.getUid());
                        firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "DeviceToken", token, new Callback() {
                            @Override
                            public void OnCallback(Object object) {

                            }
                        });
                    }
                });




        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        FirestoreManager manager = new FirestoreManager(ContentsActivity.this, "Contents", user.getUid());
        manager.read("Contents", new Callback() {
            @Override
            public void OnCallback(Object object) {
                ArrayList<Contents> contentsList = (ArrayList<Contents>)object;

                RecyclerViewAdapterForContents adapter = new RecyclerViewAdapterForContents(contentsList, ContentsActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Set category

                // Clear & refresh Dataset
                manager.read("Contents", new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        ArrayList<Contents> contentsList = (ArrayList<Contents>)object;

                        // Actually, we don't clear our dataset but create new adapter with same name
                        RecyclerViewAdapterForContents adapter = new RecyclerViewAdapterForContents(contentsList, ContentsActivity.this);
                        recyclerView.setAdapter(adapter);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });

        makeContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ContentsActivity.this, PopUpActivityForContents.class);
                startActivity(intent);

                /*
                PopupMenu popupMenu = new PopupMenu(getApplicationContext(), v);
                getMenuInflater().inflate(R.menu.contentspopup, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId() == R.id.popup1){
                            Intent intent = new Intent(ContentsActivity.this, MakeContentActivity.class);
                            startActivity(intent);
                        } else if(item.getItemId() == R.id.popup2){
                            Intent intent = new Intent(ContentsActivity.this, MakeAuctionContentActivity.class);
                            startActivity(intent);
                        } else if(item.getItemId() == R.id.popup3){

                        } else{

                        }
                        return false;
                    }
                });
                popupMenu.show();

                 */
            }
        });
    }
}