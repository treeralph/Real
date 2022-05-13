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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.real.adapter.RecyclerViewAdapterForContents;
import com.example.real.adapter.RecyclerViewAdapterForContentsV2;
import com.example.real.data.Contents;
import com.example.real.databasemanager.AssetDatabaseManager;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.InternalDatabaseManager;
import com.example.real.tool.OnSwipeTouchListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.api.Distribution;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.naver.maps.geometry.LatLng;

import org.json.JSONArray;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;

import io.grpc.Internal;

public class ContentsActivity extends AppCompatActivity {

    private String user_recent_location;
    private String user_recent_LatLng;
    private LatLng user_recent_LatLng_parsed;

    static final int CONTENTACTIVITY = 10;
    static final int CONTENTDELETEDCODE = 2;

    FirebaseUser user;
    LinearLayout chatRoomBtn;
    RecyclerView recyclerView;
    LinearLayout makeContentBtn;
    LinearLayout userHistoryBtn;
    FirestoreManager manager;
    SwipeRefreshLayout swipeRefreshLayout;

    TextView locationTextView;
    ImageView changeLocationBtn;

    EditText searchText;
    ImageView searchBtn;
    ImageView searchAdditionalBtn;

    ArrayList<Contents> ContentsList;

    RecyclerViewAdapterForContentsV2 adapter;

    AssetDatabaseManager assetDatabaseManager;
    InternalDatabaseManager internalDatabaseManager;


    String adm_cd;
    String stringLatLng;

    int flag = 0;
    int expiredFlag = 0; // 만료되지 않은 자료만 보기

    int NumPaginate = 5;
    ArrayList<String> searchArray = new ArrayList<>();
    String option_contenttype = "ANY";
    boolean option_includeexpired = true;
    int option_rectsize = 2;
    DocumentSnapshot LatestDocForPaginate;
    Query LatestQuery;

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
        locationTextView = findViewById(R.id.ContentsActivityLocationTextView);
        changeLocationBtn = findViewById(R.id.ContentsActivityChangeLocationButton);

        manager = new FirestoreManager(ContentsActivity.this, "Contents", user.getUid());
        assetDatabaseManager = new AssetDatabaseManager(this);
        internalDatabaseManager = new InternalDatabaseManager(this);

        assetDatabaseManager.read_adm_code(user_recent_location, AssetDatabaseManager.KEY_Length_3, new Callback() {
            @Override
            public void OnCallback(Object object) {
                String[] addressList = (String[]) object;
            }
        });

        internalDatabaseManager.read("user_recent_location",
                new Callback() { // success
                    @Override
                    public void OnCallback(Object object) {
                        user_recent_location = (String) object;
                        internalDatabaseManager.read("user_recent_LatLng",
                                new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        user_recent_LatLng = (String) object;
                                        String[] temp = user_recent_LatLng.split(",");
                                        user_recent_LatLng_parsed = new LatLng(Double.parseDouble(temp[0]),Double.parseDouble(temp[1]));
                                        assetDatabaseManager.adm2address(user_recent_location, new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {
                                                String address = (String) object;
                                                locationTextView.setText(address);

                                            }
                                        });
                                    }
                                },
                                new Callback() {
                                    @Override
                                    public void OnCallback(Object object) {
                                        user_recent_location = "4113565000";
                                        user_recent_LatLng = "37.389844,127.0986189";
                                        String[] temp = user_recent_LatLng.split(",");
                                        user_recent_LatLng_parsed = new LatLng(Double.parseDouble(temp[0]),Double.parseDouble(temp[1]));
                                        assetDatabaseManager.adm2address(user_recent_location, new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {
                                                String address = (String) object;
                                                locationTextView.setText(address);
                                            }
                                        });
                                    }
                                });

                    }
                },
                new Callback() { // failure
                    @Override
                    public void OnCallback(Object object) {
                        user_recent_location = "4113565000";
                        user_recent_LatLng = "37.389844,127.0986189";
                        String[] temp = user_recent_LatLng.split(",");
                        user_recent_LatLng_parsed = new LatLng(Double.parseDouble(temp[0]),Double.parseDouble(temp[1]));
                        assetDatabaseManager.adm2address(user_recent_location, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                String address = (String) object;
                                locationTextView.setText(address);
                            }
                        });
                    }
                });


        changeLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentsActivity.this, LocationActivity.class);
                startActivityForResult(intent, 1000);
            }
        });

        searchAdditionalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchAdditionalFuntionDialog dialog = new SearchAdditionalFuntionDialog(ContentsActivity.this,
                        new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                flag = 0;
                                option_contenttype = "ANY";
                                // Clear list
                                ContentsList.clear();
                                recyclerView.clearOnScrollListeners();
                                adapter.notifyDataSetChanged();
                                LatestDocForPaginate = null;
                                // Create Query
                                LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                        ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                // Read by Query
                                manager.PaginationQuery(LatestQuery, new Callback2() {
                                    @Override
                                    public void OnCallback(Object object1, Object object2) {
                                        ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                        //Collections.reverse(contentsList);
                                        LatestDocForPaginate = (DocumentSnapshot) object2;
                                        Log.d("#temp",String.valueOf(templist.size()));
                                        ContentsList.addAll(templist);
                                        adapter.notifyDataSetChanged();
                                        // update query
                                        LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                    }
                                });
                                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                        if(!recyclerView.canScrollVertically(1)) {
                                            manager.PaginationQuery(LatestQuery, new Callback2() {
                                                @Override
                                                public void OnCallback(Object object1, Object object2) {
                                                    ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                                    //Collections.reverse(contentsList);
                                                    LatestDocForPaginate = (DocumentSnapshot) object2;
                                                    Log.d("#temp",String.valueOf(templist.size()));
                                                    ContentsList.addAll(templist);
                                                    adapter.notifyItemRangeInserted(ContentsList.size()-templist.size(), templist.size());
                                                    // update query
                                                    LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                            ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                                }
                                            });
                                        }
                                    }
                                });
                            }
                        },
                        new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                flag = 1;
                                option_contenttype = "CONTENT";
                                // Clear list
                                ContentsList.clear();
                                recyclerView.clearOnScrollListeners();
                                adapter.notifyDataSetChanged();
                                LatestDocForPaginate = null;
                                // Create Query
                                LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                        ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                // Read by Query
                                manager.PaginationQuery(LatestQuery, new Callback2() {
                                    @Override
                                    public void OnCallback(Object object1, Object object2) {
                                        ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                        //Collections.reverse(contentsList);
                                        LatestDocForPaginate = (DocumentSnapshot) object2;
                                        Log.d("#temp",String.valueOf(templist.size()));
                                        ContentsList.addAll(templist);
                                        adapter.notifyDataSetChanged();
                                        // update query
                                        LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                    }
                                });
                                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                        if(!recyclerView.canScrollVertically(1)) {
                                            manager.PaginationQuery(LatestQuery, new Callback2() {
                                                @Override
                                                public void OnCallback(Object object1, Object object2) {
                                                    ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                                    //Collections.reverse(contentsList);
                                                    LatestDocForPaginate = (DocumentSnapshot) object2;
                                                    Log.d("#temp",String.valueOf(templist.size()));
                                                    ContentsList.addAll(templist);
                                                    adapter.notifyItemRangeInserted(ContentsList.size()-templist.size(), templist.size());
                                                    // update query
                                                    LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                            ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                                }
                                            });
                                        }
                                    }
                                });

                            }
                        },
                        new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                flag = 2;
                                option_contenttype = "AUCTIONCONTENT";
                                // Clear list
                                ContentsList.clear();
                                recyclerView.clearOnScrollListeners();
                                adapter.notifyDataSetChanged();
                                LatestDocForPaginate = null;
                                // Create Query
                                LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                        ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                // Read by Query
                                manager.PaginationQuery(LatestQuery, new Callback2() {
                                    @Override
                                    public void OnCallback(Object object1, Object object2) {
                                        ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                        //Collections.reverse(contentsList);
                                        LatestDocForPaginate = (DocumentSnapshot) object2;
                                        Log.d("#temp",String.valueOf(templist.size()));
                                        ContentsList.addAll(templist);
                                        adapter.notifyDataSetChanged();
                                        // update query
                                        LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                    }
                                });
                                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                        if(!recyclerView.canScrollVertically(1)) {
                                            manager.PaginationQuery(LatestQuery, new Callback2() {
                                                @Override
                                                public void OnCallback(Object object1, Object object2) {
                                                    ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                                    //Collections.reverse(contentsList);
                                                    LatestDocForPaginate = (DocumentSnapshot) object2;
                                                    Log.d("#temp",String.valueOf(templist.size()));
                                                    ContentsList.addAll(templist);
                                                    adapter.notifyItemRangeInserted(ContentsList.size()-templist.size(), templist.size());
                                                    // update query
                                                    LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                            ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                                }
                                            });
                                        }
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
                                        option_includeexpired = true;
                                        // Clear list
                                        ContentsList.clear();
                                        recyclerView.clearOnScrollListeners();
                                        adapter.notifyDataSetChanged();
                                        LatestDocForPaginate = null;
                                        // Create Query
                                        LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                        // Read by Query
                                        manager.PaginationQuery(LatestQuery, new Callback2() {
                                            @Override
                                            public void OnCallback(Object object1, Object object2) {
                                                ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                                //Collections.reverse(contentsList);
                                                LatestDocForPaginate = (DocumentSnapshot) object2;
                                                Log.d("#temp",String.valueOf(templist.size()));
                                                ContentsList.addAll(templist);
                                                adapter.notifyDataSetChanged();
                                                // update query
                                                LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                        ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                            }
                                        });
                                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                            @Override
                                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                                super.onScrollStateChanged(recyclerView, newState);
                                                if(!recyclerView.canScrollVertically(1)) {
                                                    manager.PaginationQuery(LatestQuery, new Callback2() {
                                                        @Override
                                                        public void OnCallback(Object object1, Object object2) {
                                                            ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                                            //Collections.reverse(contentsList);
                                                            LatestDocForPaginate = (DocumentSnapshot) object2;
                                                            Log.d("#temp",String.valueOf(templist.size()));
                                                            ContentsList.addAll(templist);
                                                            adapter.notifyItemRangeInserted(ContentsList.size()-templist.size(), templist.size());
                                                            // update query
                                                            LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                                    ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                                        }
                                                    });
                                                }
                                            }
                                        });
                                        break;
                                    case 1: // 만료되지 않은 자료만 보기
                                        expiredFlag = 0;
                                        option_includeexpired = false;
                                        // Clear list
                                        ContentsList.clear();
                                        recyclerView.clearOnScrollListeners();
                                        adapter.notifyDataSetChanged();
                                        LatestDocForPaginate = null;
                                        // Create Query
                                        LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                        // Read by Query
                                        manager.PaginationQuery(LatestQuery, new Callback2() {
                                            @Override
                                            public void OnCallback(Object object1, Object object2) {
                                                ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                                //Collections.reverse(contentsList);
                                                LatestDocForPaginate = (DocumentSnapshot) object2;
                                                Log.d("#temp",String.valueOf(templist.size()));
                                                ContentsList.addAll(templist);
                                                adapter.notifyDataSetChanged();
                                                // update query
                                                LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                        ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                            }
                                        });
                                        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                            @Override
                                            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                                super.onScrollStateChanged(recyclerView, newState);
                                                if(!recyclerView.canScrollVertically(1)) {
                                                    manager.PaginationQuery(LatestQuery, new Callback2() {
                                                        @Override
                                                        public void OnCallback(Object object1, Object object2) {
                                                            ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                                            //Collections.reverse(contentsList);
                                                            LatestDocForPaginate = (DocumentSnapshot) object2;
                                                            Log.d("#temp",String.valueOf(templist.size()));
                                                            ContentsList.addAll(templist);
                                                            adapter.notifyItemRangeInserted(ContentsList.size()-templist.size(), templist.size());
                                                            // update query
                                                            LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                                    ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                                        }
                                                    });
                                                }
                                            }
                                        });
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



        /*
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(searchText.getText().toString().length() != 0){
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
                            RecyclerViewAdapterForContentsV2 adapter = new RecyclerViewAdapterForContentsV2(contentsList, ContentsActivity.this);
                            recyclerView.setAdapter(adapter);
                            if(contentsList.size() == 0){
                                Toast.makeText(ContentsActivity.this, "There is no result matching for " + searchText.getText().toString()
                                        , Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

            }
        });

         */
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Contentlist Clear & Scrolllistener Clear & SearchArray Clear
                ContentsList.clear();
                recyclerView.clearOnScrollListeners();
                searchArray.clear();
                // Create Query with ST & LAT & OPTIONS
                String[] searchWordList = searchText.getText().toString().split(" ");
                for(String s: searchWordList){
                    searchArray.add(s);
                }
                LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                        ,null, option_contenttype, option_includeexpired, searchArray);
                // Ready By Query & Create Pagination Query


                manager.PaginationQuery(LatestQuery, new Callback2() {
                    @Override
                    public void OnCallback(Object object1, Object object2) {
                        ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                        //Collections.reverse(contentsList);
                        LatestDocForPaginate = (DocumentSnapshot) object2;
                        Log.d("#temp",String.valueOf(templist.size()));
                        ContentsList.addAll(templist);
                        adapter.notifyDataSetChanged();
                        // update query
                        LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                ,LatestDocForPaginate,option_contenttype,option_includeexpired, searchArray);
                        Log.d("#num : ",  String.valueOf(NumPaginate));
                        Log.d("#latlng", user_recent_LatLng_parsed.toString());
                        Log.d("#rectsize", String.valueOf(option_rectsize));
                        Log.d("#lastPage", LatestDocForPaginate.toString());

                    }
                });
                recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                        if(!recyclerView.canScrollVertically(1)) {
                            manager.PaginationQuery(LatestQuery, new Callback2() {
                                @Override
                                public void OnCallback(Object object1, Object object2) {
                                    ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                    //Collections.reverse(contentsList);
                                    LatestDocForPaginate = (DocumentSnapshot) object2;
                                    Log.d("#temp",String.valueOf(templist.size()));
                                    ContentsList.addAll(templist);
                                    adapter.notifyItemRangeInserted(ContentsList.size()-templist.size(), templist.size());
                                    // update query
                                    LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                            ,LatestDocForPaginate,option_contenttype,option_includeexpired, searchArray);
                                }
                            });
                        }
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
        /*
        manager.readlimit("Contents",3, new Callback() {
            @Override
            public void OnCallback(Object object) {
                ArrayList<Contents> contentsList = (ArrayList<Contents>)object;
                //Collections.reverse(contentsList);

                RecyclerViewAdapterForContentsV2 adapter = new RecyclerViewAdapterForContentsV2(contentsList, ContentsActivity.this);
                recyclerView.setAdapter(adapter);
            }
        });

         */
        ContentsList = new ArrayList<>();
        adapter = new RecyclerViewAdapterForContentsV2(ContentsList, ContentsActivity.this);
        recyclerView.setAdapter(adapter);

        // Initialize LatestQuery
        LatestQuery =  manager.CreatQuery("Contents",NumPaginate
                ,LatestDocForPaginate,option_contenttype,option_includeexpired);
        manager.PaginationQuery(LatestQuery, new Callback2() {
            @Override
            public void OnCallback(Object object1, Object object2) {
                ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                //Collections.reverse(contentsList);
                LatestDocForPaginate = (DocumentSnapshot) object2;
                Log.d("#temp",String.valueOf(templist.size()));
                ContentsList.addAll(templist);
                adapter.notifyDataSetChanged();
                // update query
                LatestQuery = manager.CreatQuery("Contents",NumPaginate
                        ,LatestDocForPaginate,option_contenttype,option_includeexpired);
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(!recyclerView.canScrollVertically(1)) {
                    manager.PaginationQuery(LatestQuery, new Callback2() {
                        @Override
                        public void OnCallback(Object object1, Object object2) {
                            ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                            //Collections.reverse(contentsList);
                            LatestDocForPaginate = (DocumentSnapshot) object2;
                            Log.d("#temp",String.valueOf(templist.size()));
                            Log.d("#LASTITEM",LatestDocForPaginate.get("ContentTitle").toString());
                            ContentsList.addAll(templist);
                            adapter.notifyItemRangeInserted(ContentsList.size()-templist.size(), templist.size());
                            // update query
                            LatestQuery = manager.CreatQuery("Contents",NumPaginate
                                    ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                        }
                    });
                }
            }
        });








        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //recyclerView.setVisibility(View.VISIBLE);
                // Set category
                // Clear & refresh Dataset
                ContentsList.clear();
                recyclerView.clearOnScrollListeners();

                if(searchArray.size() == 0){
                    LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                            ,null,option_contenttype,option_includeexpired);

                    manager.PaginationQuery(LatestQuery, new Callback2() {
                        @Override
                        public void OnCallback(Object object1, Object object2) {
                            ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                            //Collections.reverse(contentsList);
                            LatestDocForPaginate = (DocumentSnapshot) object2;
                            Log.d("#temp",String.valueOf(templist.size()));
                            ContentsList.addAll(templist);
                            adapter.notifyDataSetChanged();
                            // update query
                            LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                    ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                            Log.d("#num : ",  String.valueOf(NumPaginate));
                            Log.d("#latlng", user_recent_LatLng_parsed.toString());
                            Log.d("#rectsize", String.valueOf(option_rectsize));
                            Log.d("#lastPage", LatestDocForPaginate.toString());


                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if(!recyclerView.canScrollVertically(1)) {
                                manager.PaginationQuery(LatestQuery, new Callback2() {
                                    @Override
                                    public void OnCallback(Object object1, Object object2) {
                                        ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                        //Collections.reverse(contentsList);
                                        LatestDocForPaginate = (DocumentSnapshot) object2;
                                        Log.d("#temp",String.valueOf(templist.size()));
                                        ContentsList.addAll(templist);
                                        adapter.notifyItemRangeInserted(ContentsList.size()-templist.size(), templist.size());
                                        // update query
                                        LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                ,LatestDocForPaginate,option_contenttype,option_includeexpired);
                                    }
                                });
                            }
                        }
                    });
                }else{
                    LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                            ,null,option_contenttype,option_includeexpired, searchArray);

                    manager.PaginationQuery(LatestQuery, new Callback2() {
                        @Override
                        public void OnCallback(Object object1, Object object2) {
                            ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                            //Collections.reverse(contentsList);
                            LatestDocForPaginate = (DocumentSnapshot) object2;
                            Log.d("#temp",String.valueOf(templist.size()));
                            ContentsList.addAll(templist);
                            adapter.notifyDataSetChanged();
                            // update query
                            LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                    ,LatestDocForPaginate,option_contenttype,option_includeexpired, searchArray);
                            Log.d("#num : ",  String.valueOf(NumPaginate));
                            Log.d("#latlng", user_recent_LatLng_parsed.toString());
                            Log.d("#rectsize", String.valueOf(option_rectsize));
                            Log.d("#lastPage", LatestDocForPaginate.toString());


                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                    recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                        @Override
                        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                            super.onScrollStateChanged(recyclerView, newState);
                            if(!recyclerView.canScrollVertically(1)) {
                                manager.PaginationQuery(LatestQuery, new Callback2() {
                                    @Override
                                    public void OnCallback(Object object1, Object object2) {
                                        ArrayList<Contents> templist = (ArrayList<Contents>) object1;
                                        //Collections.reverse(contentsList);
                                        LatestDocForPaginate = (DocumentSnapshot) object2;
                                        Log.d("#temp",String.valueOf(templist.size()));
                                        ContentsList.addAll(templist);
                                        adapter.notifyItemRangeInserted(ContentsList.size()-templist.size(), templist.size());
                                        // update query
                                        LatestQuery = manager.CreatQuery("Contents",NumPaginate,user_recent_LatLng_parsed,option_rectsize
                                                ,LatestDocForPaginate,option_contenttype,option_includeexpired, searchArray);
                                    }
                                });
                            }
                        }
                    });
                }


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

                        RecyclerViewAdapterForContentsV2 adapter = new RecyclerViewAdapterForContentsV2(contentsList, ContentsActivity.this);
                        recyclerView.setAdapter(adapter);
                    }
                });
            }
        }else if(requestCode == 1000){
            if(resultCode == RESULT_OK){
                try{
                    String location = data.getStringExtra("Location");
                    adm_cd = data.getStringExtra("Adm_cd");
                    stringLatLng = data.getStringExtra("LatLng");
                    assetDatabaseManager.adm2address(adm_cd, new Callback() {
                        @Override
                        public void OnCallback(Object object) {
                            String address = (String) object;
                            locationTextView.setText(address);
                            internalDatabaseManager.write("user_recent_location", adm_cd, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    internalDatabaseManager.write("user_recent_LatLng", stringLatLng, new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {
                                            user_recent_LatLng = stringLatLng;
                                            user_recent_location = adm_cd;
                                            String[] temp1 = user_recent_LatLng.split(",");
                                            user_recent_LatLng_parsed = new LatLng(Double.parseDouble(temp1[0]),Double.parseDouble(temp1[1]));
                                            // todo: for test part
                                            String[] temp = stringLatLng.split(",");
                                            double lat = Double.parseDouble(temp[0]);
                                            double lng = Double.parseDouble(temp[1]);
                                            LatLng latLng = new LatLng(lat, lng);

                                            manager.readWithLocation("Contents", latLng, new Callback() {
                                                @Override
                                                public void OnCallback(Object object) {

                                                    ArrayList<Contents> contentsArrayList = (ArrayList<Contents>) object;

                                                    TextView textView = findViewById(R.id.locationtestpleasedelete);
                                                    textView.setText(String.valueOf(latLng.latitude) + "\n" + String.valueOf(latLng.longitude) + "\n");
                                                    for(Contents contents: contentsArrayList){
                                                        textView.append(contents.getContentTitle() + "\n");
                                                    }

                                                }
                                            });
                                        }
                                    });
                                }
                            });
                        }
                    });
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