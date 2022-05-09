package com.example.real;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.real.adapter.ExpandableListAdapter;
import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.data.AuctionContent;
import com.example.real.data.Comment;
import com.example.real.data.Content;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.RealTimeDatabaseManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.fragment.ImgViewFromGalleryFragment;
import com.example.real.tool.CreatePaddle;
import com.example.real.tool.NumberingMachine;
import com.example.real.tool.TimeTextTool;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.real.adapter.ExpandableListAdapter.CHILD;

public class AuctionContentActivity extends AppCompatActivity {

    static final int FLICKERNEGATIVE = -1;
    static final int FLICKERPOSITIVE = 1;

    TextView AuctionContentTitleTextView;
    TextView AuctionContentUserProfileInfoTextView;
    TextView AuctionContentTimeTextView;
    TextView AuctionContentDescriptionTextView;
    TextView AuctionContentLocationTextView;
    ViewPager AuctionContentViewPager;
    ImageView AuctionContentUserProfileImgImageView;
    TextView AuctionContentCurrentPriceTextView;
    CardView AuctionContentBidBtn;
    Button AuctionContentMessageBtn;
    TextView AuctionContentRemainingTimeTextView;
    EditText AuctionContentCommentEditText;
    Button AuctionContentCommentBtn;
    RecyclerView AuctionComments_Recyclerview;
    Button srtbtn;

    LinearLayout ContentsBtn;
    LinearLayout UserhistoryBtn;
    LinearLayout ChatRoomBtn;
    LinearLayout ModifyBtn;
    LinearLayout LatestLL;

    ImageView AuctionContentViewPagerBackgroudImageView;
    ImageView AuctionContentLikeFlicker;
    ViewPagerAdapter adapter;
    DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
    LocalDateTime tempEndTime;

    MyHandler handler;
    Thread thread;

    String userUID;
    String contentUID;
    String contentTime;
    String contentId;

    ImageView AuctionContentUserPaddleImageView;
    Bitmap UserBackground;
    Bitmap UserCenter;
    Bitmap UserHandle;
    Bitmap TempBackground;
    Bitmap TempCenter;
    Bitmap TempHandle;
    CreatePaddle createPaddle;
    TextView LatestBidderTextview;
    TextView LatestTimeTextView;
    TextView LatestPriceTextView;
    ImageView LatestBidderImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_auction_content);
        setContentView(R.layout.activity_auction_content_design);

        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        contentId = getIntent().getStringExtra("ContentId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); userUID = user.getUid();

        FirestoreManager firestoreManagerForAuctionContent = new FirestoreManager(
                AuctionContentActivity.this, "AuctionContent", user.getUid());
        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                AuctionContentActivity.this, "UserProfile", user.getUid());
        StorageManager storageManagerForAuctionContent = new StorageManager(
                AuctionContentActivity.this, "Image", user.getUid());
        StorageManager storageManagerForUserProfile = new StorageManager(
                AuctionContentActivity.this, "UserProfileImage", user.getUid());
        StorageManager storageManagerForUserPaddle = new StorageManager(
                AuctionContentActivity.this,"UserPaddleImage", user.getUid());
        RealTimeDatabaseManager realTimeDatabaseManagerForLatestBidder = new RealTimeDatabaseManager(
                AuctionContentActivity.this,"AuctionContentBidder",user.getUid());


        AuctionContentTitleTextView = findViewById(R.id.AuctionContentActivityTitleTextViewDesign);
        AuctionContentUserProfileInfoTextView = findViewById(R.id.AuctionContentActivityProfileInfoTextViewDesign);
        AuctionContentTimeTextView = findViewById(R.id.AuctionContentActivityTimeTextViewDesign);
        AuctionContentDescriptionTextView = findViewById(R.id.AuctionContentActivityDescriptionTextViewDesign);
        AuctionContentViewPager = findViewById(R.id.AuctionContentActivityContentImageViewPagerDesign);
        AuctionContentUserProfileImgImageView = findViewById(R.id.AuctionContentActivityProfileImageImageViewDesign);
        AuctionContentMessageBtn = findViewById(R.id.AuctionContentActivityMessageButtonDesign);
        AuctionContentLocationTextView = findViewById(R.id.AuctionContentActivityLocationTextView);
        AuctionContentCurrentPriceTextView = findViewById(R.id.AuctionContentActivityAuctionPriceDesign);
        AuctionContentBidBtn = findViewById(R.id.AuctionContentActivityAuctionBidButtonDesign);
        AuctionContentRemainingTimeTextView = findViewById(R.id.AuctionContentActivityRemainingTimeDesign);
        AuctionComments_Recyclerview = findViewById(R.id.AuctionContentActivityCommentRecyclerViewDesign);
        AuctionContentCommentEditText = findViewById(R.id.AuctionContentActivityCommentEditTextDesign);
        AuctionContentCommentBtn = findViewById(R.id.AuctionContentActivityCommentButtonDesign);
        AuctionContentViewPagerBackgroudImageView = findViewById(R.id.AuctionContentActivityViewPagerBackgroudImageView);
        AuctionContentUserPaddleImageView = findViewById(R.id.AuctionContentActivitypaddleIV);
        AuctionContentLikeFlicker = findViewById(R.id.desingAuctionContentLikeFlicker);
        LatestBidderTextview = findViewById(R.id.AuctionContentActivityLatestBidderTextViewDesign);
        LatestTimeTextView = findViewById(R.id.AuctionContentActivityLatestTimeTextViewDesign);
        LatestPriceTextView = findViewById(R.id.AuctionContentActivityLatestPriceTextViewDesign);
        LatestBidderImageView = findViewById(R.id.AuctionContentActivityLatestBidderImageViewDesign);
        LatestLL = findViewById(R.id.AuctionContentActivityLatestLL);
        srtbtn = findViewById(R.id.AuctionContentActivitySortingButtonDesign);


        ContentsBtn = findViewById(R.id.AuctionContentActivityContentsButtonDesign);
        UserhistoryBtn = findViewById(R.id.AuctionContentActivityUserHistoryButtonDesign);
        ChatRoomBtn = findViewById(R.id.AuctionContentActivityChatRoomButtonDesign);
        ModifyBtn = findViewById(R.id.AuctionContentActivityModifyButtonDesign);

        ContentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        UserhistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuctionContentActivity.this, UserhistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ChatRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuctionContentActivity.this, ChattingRoomActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ModifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuctionContentActivity.this, PopUpActivity.class);
                intent.putExtra("ContentID", contentId);
                intent.putExtra("ContentUID", contentUID);
                intent.putExtra("ContentTime", contentTime);
                startActivityForResult(intent, 0);
            }
        });

        AuctionContentUserProfileImgImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Bitmap bitmap = ((BitmapDrawable) AuctionContentUserProfileImgImageView.getDrawable()).getBitmap();
                //ByteArrayOutputStream stream = new ByteArrayOutputStream();
                //bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                //byte[] byteArray = stream.toByteArray();

                Intent intent = new Intent(AuctionContentActivity.this, PeekUserProfileActivity.class);
                intent.putExtra("userProfileUID", contentUID);
                //intent.putExtra("userProfileImageByteArray", byteArray);

                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View, String>(AuctionContentUserProfileImgImageView, "testView");
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(AuctionContentActivity.this, pairs);

                startActivity(intent, options.toBundle());
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        try {
            byte[] bytes = getIntent().getByteArrayExtra("ImageBitmap");
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            AuctionContentViewPagerBackgroudImageView.setImageBitmap(imageBitmap);
        }catch(Exception e){
            e.printStackTrace();
        }

        final int[] w = {FLICKERNEGATIVE};
        AuctionContentLikeFlicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(w[0] == FLICKERNEGATIVE){// 하트가 비어있는 상태에서 버튼을 누르면
                    // DB에 UserLog Write
                    FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                            AuctionContentActivity.this, "UserProfile", user.getUid());
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
                                init.addProperty("Type","Like");
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
                                temp.addProperty("Type", "Like");
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

                    // Write 끝나면 콜백에 w값을 바꿔주고 이미지변경
                    w[0] = FLICKERPOSITIVE;
                    AuctionContentLikeFlicker.setImageResource(R.drawable.new_heart_red);
                }
                else{                       // 하트가 차있는 상태에서 버튼을 누르면
                    // DB에 UserLog Search & Delete
                    FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                            AuctionContentActivity.this, "UserProfile", user.getUid());
                    firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
                        @Override
                        public void OnCallback(Object object) {
                            UserProfile userprofile = (UserProfile) object;
                            String userlog = userprofile.getUserLog();
                            String address = "Content/" + contentId;

                            // Parsing JsonArray
                            JsonParser parser = new JsonParser();
                            Object tempparsed = parser.parse(userlog);
                            JsonArray templog = (JsonArray) tempparsed;

                            // Create Json Obj
                            JsonObject temp = new JsonObject();
                            temp.addProperty("Type", "Like");
                            temp.addProperty("Address",address);

                            // Delete & Update
                            templog.remove(temp);
                            firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "UserLog",
                                    templog.toString(), new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {

                                        }
                                    });
                        }
                    });


                    // Delete 끝나면 콜백에 w값을 바꿔주고 이미지변경
                    w[0] = FLICKERNEGATIVE;
                    AuctionContentLikeFlicker.setImageResource(R.drawable.new_heart_empty);
                }

            }
        });

        AuctionContentMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String databasePath = "/Messages/" + contentId + "/" + userUID;
                Intent intent = new Intent(AuctionContentActivity.this, ChattingActivity.class);
                intent.putExtra("databasePath", databasePath);
                intent.putExtra("contentId", contentId);
                intent.putExtra("userUID", userUID);
                intent.putExtra("contentUID", contentUID);
                startActivity(intent);
            }
        });

        AuctionContentBidBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!LocalDateTime.now().isAfter(tempEndTime)) {
                    Intent intent = new Intent(AuctionContentActivity.this, AuctionBidActivity.class);
                    intent.putExtra("contentId", contentId);
                    //startActivity(intent);
                    startActivityForResult(intent, 0);
                    overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                }else{
                    Toast.makeText(AuctionContentActivity.this, "This content is expired", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Paddle Read

        createPaddle = new CreatePaddle(AuctionContentActivity.this, user.getUid());
        createPaddle.Initializer(user.getUid(), new Callback() {
            @Override
            public void OnCallback(Object object) {
                List<Bitmap> bitmapList = (List<Bitmap>) object;
                AuctionContentUserPaddleImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        int Paddle_Size_x = AuctionContentUserPaddleImageView.getWidth();
                        Bitmap InitialPaddle = createPaddle.createPaddle(bitmapList.get(0),bitmapList.get(1),bitmapList.get(2),Paddle_Size_x);
                        AuctionContentUserPaddleImageView.setScaleType(ImageView.ScaleType.FIT_XY);
                        AuctionContentUserPaddleImageView.setAdjustViewBounds(true);
                        AuctionContentUserPaddleImageView.setImageBitmap(InitialPaddle);


                    }
                });
            }
        });


        // Bidder Latest Read
        realTimeDatabaseManagerForLatestBidder.readBidderLooper(contentId, new Callback() {
            @Override
            public void OnCallback(Object object) {
                String lastTime = (String) object;
                TimeTextTool TTT = new TimeTextTool(lastTime);
                LatestTimeTextView.setText(TTT.Time3Text());
                if(lastTime != null){
                    LatestLL.setVisibility(View.VISIBLE);
                    firestoreManagerForAuctionContent.read("Content", contentId, new Callback() {
                        @Override
                        public void OnCallback(Object object) {
                            AuctionContent tempAuctionContent = (AuctionContent)object;
                            List<String> templist = tempAuctionContent.getAuctionUserList();
                            String LatestData = templist.get(templist.size()-1);
                            String LatestBidder = LatestData.split("#")[0];
                            String LatestPrice = LatestData.split("#")[2];
                            LatestPriceTextView.setText(LatestPrice);
                            AuctionContentCurrentPriceTextView.setText(LatestPrice);
                            firestoreManagerForUserProfile.read("UserProfile", LatestBidder, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    UserProfile x = (UserProfile) object;
                                    LatestBidderTextview.setText(x.getNickName());
                                }
                            });
                            createPaddle.Initializer(LatestBidder, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    List<Bitmap> bitmapList = (List<Bitmap>) object;
                                    LatestBidderImageView.post(new Runnable() {
                                        @Override
                                        public void run() {

                                            LatestBidderImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                            int Paddle_Size_x = LatestBidderImageView.getWidth();
                                            createPaddle = new CreatePaddle(AuctionContentActivity.this, user.getUid());
                                            Bitmap InitialPaddle = createPaddle.createPaddle(bitmapList.get(0),bitmapList.get(1),bitmapList.get(2),Paddle_Size_x, 30);
                                            //LatestBidderImageView.setAdjustViewBounds(true);

                                            Matrix matrix = new Matrix();
                                            matrix.postTranslate(0,50);
                                            Bitmap TopPaddingPaddle = Bitmap.createBitmap(InitialPaddle.getWidth(), InitialPaddle.getHeight(), Bitmap.Config.ARGB_8888);
                                            Canvas canvas = new Canvas(TopPaddingPaddle);
                                            canvas.drawBitmap(InitialPaddle,matrix,null);

                                            LatestBidderImageView.setImageBitmap(TopPaddingPaddle);
                                        }});
                                }});
                        }});
                }}});

        firestoreManagerForAuctionContent.read("Content", contentId, new Callback() {
            @Override
            public void OnCallback(Object object) {
                AuctionContent tempAuctionContent = (AuctionContent)object;

                String uid = tempAuctionContent.getUid(); contentUID = uid;
                String title = tempAuctionContent.getTitle();
                String description = tempAuctionContent.getContent();
                String time = tempAuctionContent.getTime(); contentTime = time;
                String endTime = tempAuctionContent.getAuctionEndTime();
                String price = tempAuctionContent.getPrice();
                String location = tempAuctionContent.getLocation();

                String year = time.substring(0, 4);
                String month = time.substring(4, 6);
                String day = time.substring(6, 8);
                String hour = time.substring(8, 10);
                String min = time.substring(10, 12);

                AuctionContentTitleTextView.setText(title);
                AuctionContentTimeTextView.setText(year + "." + month + "." + day + " - " + hour + ":" + min);
                AuctionContentDescriptionTextView.setText(description);
                AuctionContentCurrentPriceTextView.setText(price);
                AuctionContentLocationTextView.setText(location);

                tempEndTime = LocalDateTime.parse(endTime, formatter);

                handler = new MyHandler();
                thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LocalDateTime now = LocalDateTime.now();
                        if(!now.isAfter(tempEndTime)) {
                            while (true) {
                                try {
                                    Thread.sleep(1000);
                                    now = LocalDateTime.now();
                                    Duration duration = Duration.between(now, tempEndTime);
                                    int seconds = Math.abs((int) duration.getSeconds());
                                    int hour = 0;
                                    int minute = 0;
                                    int second = 0;
                                    hour = (int) (seconds / 3600);
                                    seconds = seconds - 3600 * hour;
                                    minute = (int) (seconds / 60);
                                    seconds = seconds - 60 * minute;
                                    second = seconds;

                                    Message message = Message.obtain();
                                    message.obj = String.valueOf(hour) + " : " + String.valueOf(minute) + " : " + String.valueOf(second);
                                    handler.sendMessage(message);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }else{
                            Message expiredMessage = Message.obtain();
                            expiredMessage.obj = "EXPIRED";
                            handler.sendMessage(expiredMessage);
                        }
                    }
                });
                thread.start();

                firestoreManagerForUserProfile.read("UserProfile", uid, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        UserProfile tempUserProfile = (UserProfile)object;
                        String nickname = tempUserProfile.getNickName();

                        AuctionContentUserProfileInfoTextView.setText(nickname);

                        // Userlog 읽어오고 특정 엘레멘트가 존재하는지 확인
                        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                                AuctionContentActivity.this, "UserProfile", user.getUid());
                        firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                UserProfile userprofile = (UserProfile) object;
                                String userlog = userprofile.getUserLog();
                                String address = "Content/" + contentId;

                                // Parsing JsonArray
                                JsonParser parser = new JsonParser();
                                Object tempparsed = parser.parse(userlog);
                                if(!userlog.equals("")){
                                    JsonArray templog = (JsonArray) tempparsed;

                                    // Search Obj in Array
                                    for (JsonElement shard : templog){
                                        String shardtype = shard.getAsJsonObject().get("Type").getAsString();
                                        String shardaddress = shard.getAsJsonObject().get("Address").getAsString();
                                        System.out.println(shardtype + " * " + shardaddress);
                                        if(shardtype.equals("Like") & shardaddress.equals(address)){
                                            AuctionContentLikeFlicker.setImageResource(R.drawable.new_heart_red);
                                            w[0] = FLICKERPOSITIVE;
                                            break;
                                        }
                                    }
                                }

                            }
                        });
                        storageManagerForUserProfile.downloadImg2View("UserProfileImage", uid, AuctionContentUserProfileImgImageView, new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference ref = storage.getReference();
                                StorageReference sRef = ref.child("image/" + contentId + "/1000");
                                sRef.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                    @Override
                                    public void onSuccess(ListResult listResult) {
                                        for (StorageReference itemRef : listResult.getItems()) {
                                            ImgViewFromGalleryFragment fragment = new ImgViewFromGalleryFragment(itemRef, new Callback() {
                                                @Override
                                                public void OnCallback(Object object) {
                                                    ImageView fragmentImageView = (ImageView)object;
                                                    fragmentImageView.setOnClickListener(new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View v) {
                                                            Intent imgIntent = new Intent(AuctionContentActivity.this, ImageViewActivity.class);
                                                            imgIntent.putExtra("contentId", contentId);
                                                            startActivity(imgIntent);
                                                        }
                                                    });
                                                }
                                            });
                                            adapter.addItem(fragment);
                                            AuctionContentViewPager.setAdapter(adapter);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        e.printStackTrace();
                                    }
                                });
                            }
                        });
                    }
                });
            }
        });


        AuctionComments_Recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<ExpandableListAdapter.Item> data_auction = new ArrayList<>();
        ExpandableListAdapter expandablelistadapterForAuction = new ExpandableListAdapter(data_auction, AuctionContentActivity.this, contentId);
        AuctionComments_Recyclerview.setAdapter(expandablelistadapterForAuction);

        String Contentuseruid = contentUID;
        String handphoneuseruid = user.getUid();

        FirestoreManager firestoreManagerForComment = new FirestoreManager(AuctionContentActivity.this, "Comment", user.getUid());

        String temp_path = "Content/" + contentId + "/Comments";

        firestoreManagerForComment.read(temp_path, new Callback() {
            @Override
            public void OnCallback(Object object) {
                ArrayList<Comment> X = (ArrayList<Comment>) object;
                if(X.size() == 0){
                    AuctionComments_Recyclerview.setVisibility(View.GONE);
                }

                for (Comment QuaryComment : X) {

                    int CommentType;
                    if (QuaryComment.getRecomment_token().equals("0")) {

                        CommentType = ExpandableListAdapter.BACHELOR;

                        data_auction.add(new ExpandableListAdapter.Item(CommentType, QuaryComment.getFrom(), QuaryComment.getTo(), QuaryComment.getTime(), QuaryComment.getMention(), QuaryComment.getRecomment_token()));
                        expandablelistadapterForAuction.notifyDataSetChanged();
                    } else {
                        CommentType = ExpandableListAdapter.HEADER;
                        ExpandableListAdapter.Item temp_header = new ExpandableListAdapter.Item(CommentType, QuaryComment.getFrom(), QuaryComment.getTo(), QuaryComment.getTime(), QuaryComment.getMention(), QuaryComment.getRecomment_token());
                        //data_auction.add(temp_header);
                        //final int[] count = {0};

                        // RECOMMENTS 읽어오기
                        firestoreManagerForComment.read(temp_path + "/" + QuaryComment.getTime() + "/Recomments", new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                ArrayList<Comment> Y = (ArrayList<Comment>) object;

                                int Y_length = Y.size();
                                NumberingMachine machine = new NumberingMachine();

                                //data_auction.add(temp_header);
                                for (Comment QuaryRecomment : Y) {
                                    machine.add();
                                    int RecommentType = CHILD;
                                    System.out.println(temp_header.from);
                                    ExpandableListAdapter.Item temp_child = new ExpandableListAdapter.Item(RecommentType, QuaryRecomment.getFrom(), QuaryRecomment.getTo(), QuaryRecomment.getTime(), QuaryRecomment.getMention(), QuaryRecomment.getRecomment_token());
                                    System.out.println(temp_child.from);
                                    temp_header.invisibleChildren.add(temp_child);
                                    /*
                                    if (count[0] == 0) {
                                        data_auction.add(temp_header);
                                        count[0] = 1; }*/

                                    //expandablelistadapterForAuction.notifyDataSetChanged();
                                    if(machine.getNumber() == Y_length){
                                        data_auction.add(temp_header);
                                        expandablelistadapterForAuction.sorting();
                                        AuctionComments_Recyclerview.setAdapter(expandablelistadapterForAuction);

                                    }
                                }
                            }
                        });
                        expandablelistadapterForAuction.notifyDataSetChanged();
                    }
                }
            }
        });

        AuctionContentCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp_mention = AuctionContentCommentEditText.getText().toString();
                if(temp_mention.isEmpty()){}
                else{

                    //Log.d("ERRORFIND1", user.getUid());
                    //Log.d("ERRORFIND - contentUID", contentUID);

                    Comment temp_comment = new Comment(user.getUid(), contentUID, temp_mention);


                    firestoreManagerForComment.write(temp_comment, temp_path, temp_comment.getTime(), new Callback() {
                        @Override
                        public void OnCallback(Object object) {
                            firestoreManagerForUserProfile.read("UserProfile", user.getUid(), new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    UserProfile userprofile = (UserProfile) object;
                                    String userlog = userprofile.getUserLog();
                                    String address = "Content/" + contentId +"/Comments/" + temp_comment.getTime();
                                    if(userlog.equals("")){
                                        // Create Json Obj & JsonArray
                                        JsonArray frame = new JsonArray();
                                        JsonObject init = new JsonObject();
                                        init.addProperty("Type","Comment");
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
                                        temp.addProperty("Type", "Comment");
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
                            data_auction.add(new ExpandableListAdapter.Item(ExpandableListAdapter.BACHELOR, user.getUid(), contentUID, temp_comment.getTime(), temp_mention, "0"));
                            expandablelistadapterForAuction.notifyDataSetChanged();
                            AuctionComments_Recyclerview.setAdapter(expandablelistadapterForAuction);
                            AuctionComments_Recyclerview.setVisibility(View.VISIBLE);

                        }
                    });
                }
            }
        });

        srtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandablelistadapterForAuction.sorting();
                AuctionComments_Recyclerview.setAdapter(expandablelistadapterForAuction);
                // should use notifyitemmoved?

            }
        });

    }

    class MyHandler extends Handler{
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            String tempText = (String)msg.obj;
            if(tempText.equals("EXPIRED")) {
                AuctionContentRemainingTimeTextView.setText("EXPIRED AUCTION");
            }else {
                AuctionContentRemainingTimeTextView.setText(tempText);
            }
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        String contentId = getIntent().getStringExtra("ContentId");
        String temp_path = "Content/" + contentId + "/Comments";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        List<ExpandableListAdapter.Item> data_auction = new ArrayList<>();
        ExpandableListAdapter expandablelistadapterForAuction = new ExpandableListAdapter(data_auction, AuctionContentActivity.this, contentId);
        AuctionComments_Recyclerview.setAdapter(expandablelistadapterForAuction);

        FirestoreManager firestoreManagerForComment = new FirestoreManager(AuctionContentActivity.this, "Comment", user.getUid());
        firestoreManagerForComment.read(temp_path, new Callback() {
            @Override
            public void OnCallback(Object object) {
                ArrayList<Comment> X = (ArrayList<Comment>) object;

                for (Comment QuaryComment : X) {

                    int CommentType;
                    if (QuaryComment.getRecomment_token().equals("0")) {

                        CommentType = ExpandableListAdapter.BACHELOR;

                        data_auction.add(new ExpandableListAdapter.Item(CommentType, QuaryComment.getFrom(), QuaryComment.getTo(), QuaryComment.getTime(), QuaryComment.getMention(), QuaryComment.getRecomment_token()));
                        expandablelistadapterForAuction.notifyDataSetChanged();
                    } else {
                        CommentType = ExpandableListAdapter.HEADER;
                        ExpandableListAdapter.Item temp_header = new ExpandableListAdapter.Item(CommentType, QuaryComment.getFrom(), QuaryComment.getTo(), QuaryComment.getTime(), QuaryComment.getMention(), QuaryComment.getRecomment_token());
                        //data_auction.add(temp_header);
                        final int[] count = {0};

                        // RECOMMENTS 읽어오기
                        firestoreManagerForComment.read(temp_path + "/" + QuaryComment.getTime() + "/Recomments", new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                ArrayList<Comment> Y = (ArrayList<Comment>) object;
                                //data_auction.add(temp_header);
                                for (Comment QuaryRecomment : Y) {
                                    int RecommentType = CHILD;

                                    if (count[0] == 0) {
                                        data_auction.add(temp_header);
                                        count[0] = 1;
                                    }
                                    data_auction.add(new ExpandableListAdapter.Item(RecommentType, QuaryRecomment.getFrom(), QuaryRecomment.getTo(), QuaryRecomment.getTime(), QuaryRecomment.getMention(), QuaryRecomment.getRecomment_token()));
                                    expandablelistadapterForAuction.notifyDataSetChanged();

                                }
                            }
                        });
                        expandablelistadapterForAuction.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==0){
            FirestoreManager tempFirestoreManager = new FirestoreManager(
                    this, "AuctionContent", userUID);

            tempFirestoreManager.read("Content", contentId, new Callback() {
                @Override
                public void OnCallback(Object object) {
                    AuctionContent tempContent = (AuctionContent) object;
                    String newAuctionPrice = tempContent.getPrice();
                    AuctionContentCurrentPriceTextView.setText(newAuctionPrice);

                    if(resultCode == 100) {
                        ArrayList<String> auctionUserList = tempContent.getAuctionUserList();
                        String myBidPrice = data.getStringExtra("MyBidPrice");
                        String auctionUserListFormat = userUID + "#" + myBidPrice;

                        if (auctionUserList.contains(auctionUserListFormat)) {
                            Toast.makeText(AuctionContentActivity.this, "Your Bid is successful!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(AuctionContentActivity.this, "Your Bid is failure!", Toast.LENGTH_LONG).show();
                        }
                    }
                }
            });
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

}