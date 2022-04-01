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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.real.data.AuctionContent;
import com.example.real.data.MutexLock;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.RealTimeDatabaseManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.tool.CreatePaddle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

public class AuctionBidActivity extends AppCompatActivity {

    TextView AuctionPriceEditText;
    TextView AuctionCurrentPriceTextView;
    TextView AuctionPriceGapTextView;
    ImageView UserPaddleImageView;
    CardView AuctionPriceBidBtn;

    final DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();

    Bitmap UserBackground;
    Bitmap UserCenter;
    Bitmap UserHandle;
    CreatePaddle createPaddle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_auction_bid);
        setContentView(R.layout.activity_auction_bid_design);

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://real-95e0b-default-rtdb.asia-southeast1.firebasedatabase.app/");
        DatabaseReference dRef = mDatabase.getReference("/MutexLock");
        dRef.runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                return null;
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

            }
        });


        String contentId = getIntent().getStringExtra("contentId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        FirestoreManager firestoreManagerForAuctionContent = new FirestoreManager(AuctionBidActivity.this, "AuctionContent", user.getUid());
        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(AuctionBidActivity.this, "UserProfile", user.getUid());
        StorageManager storageManagerForUserPaddle = new StorageManager(
                AuctionBidActivity.this,"UserPaddleImage", user.getUid());
        RealTimeDatabaseManager realTimeDatabaseManagerForAuctionBidder = new RealTimeDatabaseManager(AuctionBidActivity.this,"AuctionContentBidder", user.getUid());

        AuctionPriceEditText = findViewById(R.id.AuctionBidActivityBiddingPriceEditText);
        AuctionPriceBidBtn = findViewById(R.id.AuctionBidActivityBidButton);
        AuctionCurrentPriceTextView = findViewById(R.id.AuctionBidActivityCurrentPriceTextView);
        AuctionPriceGapTextView = findViewById(R.id.AuctionBidActivityPriceGapTextView);
        UserPaddleImageView = findViewById(R.id.AuctionBidActivityUserPaddleImageView);

        firestoreManagerForAuctionContent.read("Content", contentId, new Callback() {
            @Override
            public void OnCallback(Object object) {
                AuctionContent auctionContent = (AuctionContent)object;
                Long bidPrice = Long.parseLong(auctionContent.getPrice()) + Long.parseLong(auctionContent.getPriceGap());
                AuctionPriceEditText.setText(String.valueOf(bidPrice));
                AuctionCurrentPriceTextView.setText(auctionContent.getPrice());
                AuctionPriceGapTextView.setText(auctionContent.getPriceGap());
            }
        });

        AuctionPriceBidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firestoreManagerForAuctionContent.read("Content", contentId, new Callback() {
                    @Override
                    public void OnCallback(Object object) {

                        AuctionContent auctionContent = (AuctionContent) object;
                        ArrayList<String> userList = auctionContent.getAuctionUserList();
                        AuctionContent tempAuctionContent = null;
                        try {
                            tempAuctionContent = (AuctionContent) auctionContent.clone();
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }

                        String bidPrice = AuctionPriceEditText.getText().toString();
                        tempAuctionContent.setPrice(bidPrice);
                        tempAuctionContent.PriceGapPolicy(bidPrice);
                        String priceGap = tempAuctionContent.getPriceGap();

                        // minimum bidable price
                        int threshold = Integer.parseInt(auctionContent.getPrice()) + Integer.parseInt(auctionContent.getPriceGap());
                        if (Integer.parseInt(bidPrice) >= threshold) {

                            firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    UserProfile userProfile = (UserProfile) object;
                                    String deviceToken = userProfile.getDeviceToken();
                                    String userListItem = user.getUid() + "#" + deviceToken + "#" + bidPrice; // todo: append userDeviceToken

                                    FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://real-95e0b-default-rtdb.asia-southeast1.firebasedatabase.app/");
                                    DatabaseReference dRef = mDatabase.getReference("/MutexLock/" + contentId);
                                    dRef.runTransaction(new Transaction.Handler() {
                                        @NonNull
                                        @Override
                                        public Transaction.Result doTransaction(@NonNull MutableData currentData) { // transaction retry at this function.
                                            // todo: request to acquire mutex lock flag from real time database, then
                                            Log.d("AuctionBidActivity_TEST_doTransaction", currentData.toString());
                                            MutexLock mutexLock = currentData.getValue(MutexLock.class);
                                            if (mutexLock == null) {
                                                Log.d("AuctionBidActivity_TEST_doTransaction1", "Transaction error");
                                                return Transaction.success(currentData);
                                            }else {
                                                String flag = mutexLock.getFlag();
                                                String time = mutexLock.getTime();
                                                LocalDateTime timeLocalDateTime = LocalDateTime.parse(time, formatter);
                                                LocalDateTime now = LocalDateTime.now();
                                                Duration duration = Duration.between(timeLocalDateTime, now);
                                                int seconds = Math.abs((int) duration.getSeconds());

                                                if (flag.equals("0") || (flag.equals("1") && seconds >= 15)) {
                                                    Log.d("AuctionBidActivity_TEST_doTransaction2", mutexLock.getFlag());
                                                    Log.d("AuctionBidActivity_TEST", "Mutex Acquire");
                                                    MutexLock tempMutexLock = new MutexLock();

                                                    tempMutexLock.setFlag("1");
                                                    tempMutexLock.setTime(now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
                                                    currentData.setValue(tempMutexLock);
                                                    return Transaction.success(currentData);
                                                } else {
                                                    Log.d("AuctionBidActivity_TEST_doTransaction3", mutexLock.getFlag());
                                                    return Transaction.abort();
                                                }
                                            }
                                        }
                                        @Override
                                        public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                                            Log.d("AuctionBidActivity_TEST_onComplete", Boolean.toString(committed));
                                            if(committed == true && currentData != null) {
                                                Log.d("AuctionBidActivity_TEST_onComplete", currentData.toString());
                                                FirebaseFirestore db = FirebaseFirestore.getInstance();
                                                DocumentReference ref = db.document("Content/" + contentId);
                                                db.runTransaction(new com.google.firebase.firestore.Transaction.Function<Object>() {
                                                    @Nullable
                                                    @Override
                                                    public Object apply(@NonNull com.google.firebase.firestore.Transaction transaction) throws FirebaseFirestoreException {
                                                        DocumentSnapshot snapshot = transaction.get(ref);

                                                        userList.add(userListItem);

                                                        Map<String, Object> map = new HashMap<>();
                                                        map.put("price", bidPrice);
                                                        map.put("priceGap", priceGap);
                                                        map.put("auctionUserList", userList);

                                                        transaction.update(ref, map);
                                                        realTimeDatabaseManagerForAuctionBidder.writeBidder(contentId);
                                                        // Success
                                                        return null;
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<Object>() {
                                                    @Override
                                                    public void onSuccess(Object o) {
                                                        Log.d("AuctionBidActivity_TEST_OnComplete_successListener", "AuctionContent price update is successful");
                                                        Intent intent = new Intent(AuctionBidActivity.this, AuctionContentActivity.class);
                                                        intent.putExtra("ContentId", contentId);
                                                        intent.putExtra("MyBidPrice", bidPrice);
                                                        setResult(100, intent);
                                                        finish();

                                                        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance("https://real-95e0b-default-rtdb.asia-southeast1.firebasedatabase.app/");
                                                        DatabaseReference dRef = mDatabase.getReference("/MutexLock/" + contentId);
                                                        dRef.runTransaction(new Transaction.Handler() {
                                                            @NonNull
                                                            @Override
                                                            public Transaction.Result doTransaction(@NonNull MutableData currentData) {

                                                                Log.d("AuctionBidActivity_TEST_doTransaction", currentData.toString());
                                                                MutexLock mutexLock = currentData.getValue(MutexLock.class);
                                                                if (mutexLock == null) {
                                                                    Log.d("AuctionBidActivity_TEST_doTransaction1", mutexLock.getFlag());
                                                                    return Transaction.abort();
                                                                }else {
                                                                    if (mutexLock.getFlag().equals("1")) {
                                                                        Log.d("AuctionBidActivity_TEST_doTransaction2", mutexLock.getFlag());
                                                                        Log.d("AuctionBidActivity_TEST", "Mutex Acquire");
                                                                        MutexLock tempMutexLock = new MutexLock();
                                                                        tempMutexLock.setFlag("0");
                                                                        tempMutexLock.setTime(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS")));
                                                                        currentData.setValue(tempMutexLock);
                                                                        return Transaction.success(currentData);
                                                                    } else {
                                                                        Log.d("AuctionBidActivity_TEST_doTransaction3", mutexLock.getFlag());
                                                                        return Transaction.abort();
                                                                    }
                                                                }
                                                            }
                                                            @Override
                                                            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                                                            }
                                                        });
                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e("AuctionBidActivity_TEST_OnComplete_failureListener", "AuctionContent price update is failure");
                                                    }
                                                });
                                            }else{ // comitted == false

                                                Toast.makeText(AuctionBidActivity.this, "bid failure", Toast.LENGTH_LONG).show();
                                                Intent intent = new Intent(AuctionBidActivity.this, AuctionContentActivity.class);
                                                intent.putExtra("ContentId", contentId);
                                                intent.putExtra("MyBidPrice", bidPrice);
                                                setResult(100, intent);
                                                finish();
                                            }
                                        }
                                    });
                                }
                            });
                        } else {
                            Toast.makeText(AuctionBidActivity.this, "" +
                                    "Bid Price must be greater than minimum coast" + "(" + String.valueOf(threshold) + ")", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        // Paddle Read
        Bitmap Test = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
        Test.eraseColor(0xfffaeb87);
        Bitmap InitialBG = Test;
        Bitmap InitialCenter = BitmapFactory.decodeResource(this.getResources(), R.drawable.mango_flaticon_1032525);
        Bitmap InitialHandle = BitmapFactory.decodeResource(this.getResources(), R.drawable.aucto1);
        UserBackground = InitialBG;
        UserCenter = InitialCenter;
        UserHandle = InitialHandle;
        storageManagerForUserPaddle.downloadforpaddle("UserPaddleImage/" + user.getUid(), new Callback() {
            @Override
            public void OnCallback(Object object) {
                if(object == null){
                }
                else{ Map<String, Bitmap> Map = (java.util.Map<String, Bitmap>) object;
                    for(String key : Map.keySet()){
                        switch (key){
                            case "Background" : UserBackground = Map.get("Background");break;
                            case "Center" : UserCenter = Map.get("Center");break;
                            case "Handle" : UserHandle = Map.get("Handle");break;
                            default: Toast.makeText(AuctionBidActivity.this, key, Toast.LENGTH_SHORT).show();break;
                        }
                    }}

                UserPaddleImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        int Paddle_Size_x = UserPaddleImageView.getWidth();
                        createPaddle = new CreatePaddle();
                        Bitmap InitialPaddle = createPaddle.createPaddle(UserBackground,UserCenter,UserHandle,Paddle_Size_x);

                        UserPaddleImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        UserPaddleImageView.setAdjustViewBounds(true);
                        UserPaddleImageView.setImageBitmap(InitialPaddle);
                    }
                });
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
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