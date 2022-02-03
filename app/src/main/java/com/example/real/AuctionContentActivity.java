package com.example.real;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.fragment.ImgViewFromGalleryFragment;
import com.example.real.tool.NumberingMachine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

import static com.example.real.adapter.ExpandableListAdapter.CHILD;

public class AuctionContentActivity extends AppCompatActivity {

    TextView AuctionContentTitleTextView;
    TextView AuctionContentUserProfileInfoTextView;
    TextView AuctionContentTimeTextView;
    TextView AuctionContentDescriptionTextView;
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


    ViewPagerAdapter adapter;
    DateTimeFormatter formatter = new DateTimeFormatterBuilder().appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
    LocalDateTime tempEndTime;

    MyHandler handler;
    Thread thread;

    String userUID;
    String contentUID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_auction_content);
        setContentView(R.layout.activity_auction_content_design);


        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        String contentId = getIntent().getStringExtra("ContentId");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); userUID = user.getUid();

        FirestoreManager firestoreManagerForAuctionContent = new FirestoreManager(
                AuctionContentActivity.this, "AuctionContent", user.getUid());
        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                AuctionContentActivity.this, "UserProfile", user.getUid());
        StorageManager storageManagerForAuctionContent = new StorageManager(
                AuctionContentActivity.this, "Image", user.getUid());
        StorageManager storageManagerForUserProfile = new StorageManager(
                AuctionContentActivity.this, "UserProfileImage", user.getUid());

        AuctionContentTitleTextView = findViewById(R.id.AuctionContentActivityTitleTextViewDesign);
        AuctionContentUserProfileInfoTextView = findViewById(R.id.AuctionContentActivityProfileInfoTextViewDesign);
        AuctionContentTimeTextView = findViewById(R.id.AuctionContentActivityTimeTextViewDesign);
        AuctionContentDescriptionTextView = findViewById(R.id.AuctionContentActivityDescriptionTextViewDesign);
        AuctionContentViewPager = findViewById(R.id.AuctionContentActivityContentImageViewPagerDesign);
        AuctionContentUserProfileImgImageView = findViewById(R.id.AuctionContentActivityProfileImageImageViewDesign);
        AuctionContentMessageBtn = findViewById(R.id.AuctionContentActivityMessageButtonDesign);
        AuctionContentCurrentPriceTextView = findViewById(R.id.AuctionContentActivityAuctionPriceDesign);
        AuctionContentBidBtn = findViewById(R.id.AuctionContentActivityAuctionBidButtonDesign);
        AuctionContentRemainingTimeTextView = findViewById(R.id.AuctionContentActivityRemainingTimeDesign);
        AuctionComments_Recyclerview = findViewById(R.id.AuctionContentActivityCommentRecyclerViewDesign);
        AuctionContentCommentEditText = findViewById(R.id.AuctionContentActivityCommentEditTextDesign);
        AuctionContentCommentBtn = findViewById(R.id.AuctionContentActivityCommentButtonDesign);
        srtbtn = findViewById(R.id.AuctionContentActivitySortingButtonDesign);

        ViewPagerAdapter tempAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        byte[] bytes = getIntent().getByteArrayExtra("ImageBitmap");
        Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

        ImgViewFromGalleryFragment fragment = new ImgViewFromGalleryFragment(imageBitmap);
        tempAdapter.addItem(fragment);
        AuctionContentViewPager.setAdapter(tempAdapter);




        AuctionContentMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuctionContentActivity.this, ChattingActivity.class);
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
                }else{
                    Toast.makeText(AuctionContentActivity.this, "This content is expired", Toast.LENGTH_SHORT).show();
                }
            }
        });

        firestoreManagerForAuctionContent.read("Content", contentId, new Callback() {
            @Override
            public void OnCallback(Object object) {
                AuctionContent tempAuctionContent = (AuctionContent)object;

                String uid = tempAuctionContent.getUid(); contentUID = uid;
                String title = tempAuctionContent.getTitle();
                String description = tempAuctionContent.getContent();
                String time = tempAuctionContent.getTime();
                String endTime = tempAuctionContent.getAuctionEndTime();
                String price = tempAuctionContent.getPrice();

                String year = time.substring(0, 4);
                String month = time.substring(4, 6);
                String day = time.substring(6, 8);
                String hour = time.substring(8, 10);
                String min = time.substring(10, 12);

                AuctionContentTitleTextView.setText(title);
                AuctionContentTimeTextView.setText(year + "." + month + "." + day + " - " + hour + ":" + min);
                AuctionContentDescriptionTextView.setText(description);
                AuctionContentCurrentPriceTextView.setText(price);

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
                                    firestoreManagerForUserProfile.update("UserProfile", user.getUid(), "UserLog",
                                            userlog + "\n COMMENT WRITE : " + contentUID + " " + temp_comment.toString(), new Callback() {
                                                @Override
                                                public void OnCallback(Object object) {

                                                }
                                            });
                                }
                            });
                            data_auction.add(new ExpandableListAdapter.Item(ExpandableListAdapter.BACHELOR, user.getUid(), contentUID, temp_comment.getTime(), temp_mention, "0"));

                            expandablelistadapterForAuction.notifyDataSetChanged();
                            AuctionComments_Recyclerview.setAdapter(expandablelistadapterForAuction);

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
            if(resultCode==100){
                finish();
            }
        }
    }
}