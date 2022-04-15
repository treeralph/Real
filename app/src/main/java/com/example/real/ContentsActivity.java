package com.example.real;

import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.InsetDrawable;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.util.Pair;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
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
import java.util.List;
import java.util.Collections;

public class ContentsActivity extends AppCompatActivity {

    static final int CONTENTACTIVITY = 10;
    static final int CONTENTDELETEDCODE = 2;

    FirebaseUser user;
    LinearLayout chatRoomBtn;
    RecyclerView recyclerView;
    LinearLayout makeContentBtn;
    LinearLayout userHistoryBtn;
    FirestoreManager manager;
    SwipeRefreshLayout swipeRefreshLayout;

    EditText searchText;
    ImageView searchBtn;
    ImageView searchAdditionalBtn;

    int flag = 0;
    int expiredFlag = 0; // 만료되지 않은 자료만 보기

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_contents);
        setContentView(R.layout.activity_contents_design);

        try{
            String FCM_intent_to_auctionContent = getIntent().getStringExtra("FCM_contentId");
            Log.d("WOWMACHINE_ContentsActivity", FCM_intent_to_auctionContent);

            Intent intent = new Intent(ContentsActivity.this, AuctionContentActivity.class);
            intent.putExtra("ContentId", FCM_intent_to_auctionContent);
            startActivity(intent);

        } catch(Exception e){


        }

        recyclerView = findViewById(R.id.ContentsActivityRecyclerViewDesign);
        makeContentBtn = findViewById(R.id.ContentsMakeContentBtnDesign);
        user = FirebaseAuth.getInstance().getCurrentUser();
        chatRoomBtn = findViewById(R.id.ContentsActivityChatRoomBtnDesign);
        userHistoryBtn = findViewById(R.id.ContentsActivityUserHistoryButton);
        swipeRefreshLayout = findViewById(R.id.ContentsActivitySwipeRefreshLayout);
        searchText = findViewById(R.id.ContentsActivitySearchEditText);
        searchBtn = findViewById(R.id.ContentsActivitySearchButton);
        searchAdditionalBtn = findViewById(R.id.ContentsActivitySearchAdditionalButton);

        manager = new FirestoreManager(ContentsActivity.this, "Contents", user.getUid());

        searchAdditionalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchAdditionalFuntionDialog dialog = new SearchAdditionalFuntionDialog(ContentsActivity.this,
                        new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                manager.read("Contents", new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        ArrayList<Contents> contentsList = (ArrayList<Contents>) object;
                                        Collections.reverse(contentsList);
                                        RecyclerViewAdapterForContents adapter = new RecyclerViewAdapterForContents(contentsList, ContentsActivity.this);
                                        recyclerView.setAdapter(adapter);
                                        flag = 0;
                                    }
                                });
                            }
                        },
                        new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                manager.search("/Contents", "ContentType", "Content", new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        ArrayList<Contents> contentsList = (ArrayList<Contents>) object;
                                        RecyclerViewAdapterForContents adapter = new RecyclerViewAdapterForContents(contentsList, ContentsActivity.this);
                                        recyclerView.setAdapter(adapter);
                                        flag = 1;
                                    }
                                });
                            }
                        },
                        new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                manager.search("/Contents", "ContentType", "AuctionContent", new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        ArrayList<Contents> contentsList = (ArrayList<Contents>) object;
                                        RecyclerViewAdapterForContents adapter = new RecyclerViewAdapterForContents(contentsList, ContentsActivity.this);
                                        recyclerView.setAdapter(adapter);
                                        flag = 2;
                                    }
                                });
                            }
                        },
                        new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                ImageView imageView = (ImageView) object;
                                switch(expiredFlag){
                                    case 0: // 만료된 자료까지 보기
                                        expiredFlag = 1;
                                        break;
                                    case 1: // 만료되지 않은 자료만 보기
                                        expiredFlag = 0;
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }, flag, expiredFlag);

                dialog.setCanceledOnTouchOutside(true);
                dialog.setCancelable(true);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
                layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;
                dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
                dialog.show();
            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> searchArray = new ArrayList<>();
                String[] searchWordList = searchText.getText().toString().split(" ");
                for(String s: searchWordList){
                    searchArray.add(s);
                }
                Log.d("NOWTESTNOW", searchArray.toString());
                manager.searchIn("/Contents", "WordCase", searchArray, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        ArrayList<Contents> contentsList = (ArrayList<Contents>) object;
                        Log.d("NOWTESTNOW", contentsList.toString());
                        RecyclerViewAdapterForContents adapter = new RecyclerViewAdapterForContents(contentsList, ContentsActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        });



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

        //FirestoreManager manager = new FirestoreManager(ContentsActivity.this, "Contents", user.getUid());
        manager.read("Contents", new Callback() {
            @Override
            public void OnCallback(Object object) {
                ArrayList<Contents> contentsList = (ArrayList<Contents>)object;
                Collections.reverse(contentsList);

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

                        Collections.reverse(contentsList);
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
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CONTENTACTIVITY){ // ContentActivity
            if(resultCode == CONTENTDELETEDCODE){
                manager.read("Contents", new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        ArrayList<Contents> contentsList = (ArrayList<Contents>)object;
                        Collections.reverse(contentsList);

                        RecyclerViewAdapterForContents adapter = new RecyclerViewAdapterForContents(contentsList, ContentsActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                });
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

    class SearchAdditionalFuntionDialog extends Dialog {

        Callback normalSearchCallback;
        Callback contentSearchCallback;
        Callback auctionSearchCallback;
        Callback expiredLockCallback;

        int flag;
        int expiredFlag;

        public SearchAdditionalFuntionDialog(@NonNull Context context, Callback normalSearchCallback, Callback contentSearchCallback, Callback auctionSearchCallback, Callback expiredLockCallback, int flag, int expiredFlag) {

            super(context);
            this.normalSearchCallback = normalSearchCallback;
            this.contentSearchCallback = contentSearchCallback;
            this.auctionSearchCallback = auctionSearchCallback;
            this.expiredLockCallback = expiredLockCallback;
            this.flag = flag;
            this.expiredFlag = expiredFlag;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.search_additional_function_dialog);

            LinearLayout normalSearchBtn = findViewById(R.id.searchDialogNormalSort);
            LinearLayout contentSearchBtn = findViewById(R.id.searchDialogContent);
            LinearLayout auctionSearchBtn = findViewById(R.id.searchDialogAuction);
            LinearLayout expiredLockBtn = findViewById(R.id.searchDialogExpiredLockButton);

            CardView normalSearchBox = findViewById(R.id.searchDialogNormalSortBox);
            CardView contentSearchBox = findViewById(R.id.searchDialogContentBox);
            CardView auctionSearchBox = findViewById(R.id.searchDialogAuctionBox);
            ImageView expiredLockImageView = findViewById(R.id.searchDialogExpiredLockImageView);

            ArrayList<CardView> cardViewList = new ArrayList<>();
            cardViewList.add(normalSearchBox);
            cardViewList.add(contentSearchBox);
            cardViewList.add(auctionSearchBox);

            CardView targetCardView = cardViewList.get(flag);
            targetCardView.setBackgroundColor(Color.BLACK);

            if(expiredFlag == 0){
                expiredLockImageView.setImageResource(R.drawable.lock_icon_new);
            }else{
                expiredLockImageView.setImageResource(R.drawable.unlock_icon_new);
            }

            normalSearchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    normalSearchCallback.OnCallback(null);
                    dismiss();
                }
            });
            contentSearchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    contentSearchCallback.OnCallback(null);
                    dismiss();
                }
            });
            auctionSearchBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    auctionSearchCallback.OnCallback(null);
                    dismiss();
                }
            });
            expiredLockBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    expiredLockCallback.OnCallback(expiredLockImageView);
                    dismiss();
                }
            });

        }
    }
}