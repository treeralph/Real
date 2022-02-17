
package com.example.real;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.transition.Transition;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.ContentView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.example.real.adapter.ExpandableListAdapter;
import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.data.Comment;
import com.example.real.data.Content;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.fragment.ImgViewFromGalleryFragment;
import com.example.real.tool.NumberingMachine;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.squareup.okhttp.internal.DiskLruCache;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.example.real.adapter.ExpandableListAdapter.CHILD;

public class ContentActivity extends AppCompatActivity {

    static final int FLICKERNEGATIVE = -1;
    static final int FLICKERPOSITIVE = 1;
    static final int CONTENTDELETEDCODE = 2;

    TextView ContentTitleTextView;
    TextView ContentUserProfileInfoTextView;
    TextView ContentTimeTextView;
    TextView ContentDescriptionTextView;
    ViewPager ContentViewPager;
    ImageView ContentUserProfileImgImageView;
    Button ContentMessageBtn;
    ImageView ContentLikeFlicker;

    ViewPagerAdapter adapter;

    String userUID;
    String contentUID;

    String contentId;
    String contentTime;

    EditText ContentCommentEditText;
    Button ContentCommentBtn;
    Button srtbtn;

    RecyclerView Comments_Recyclerview;
    ImageView BackgroundImageView;

    LinearLayout ContentsBtn;
    LinearLayout UserhistoryBtn;
    LinearLayout ChatRoomBtn;
    LinearLayout ModifyBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_content);
        // todo: for design test
        setContentView(R.layout.activity_content_design);
        /////////////////////////////////////////////////

        ContentTitleTextView = findViewById(R.id.designTestContentTitleTextView);
        ContentUserProfileInfoTextView = findViewById(R.id.designTestContentUserProfileNickNameTextView);
        ContentTimeTextView = findViewById(R.id.designTestContentTimeTextView);
        ContentDescriptionTextView = findViewById(R.id.designTestContentDescriptionTextView);
        ContentViewPager = findViewById(R.id.designTestContentImageViewPager); //
        ContentUserProfileImgImageView = findViewById(R.id.designTestContentUserProfileImageImageView);
        ContentMessageBtn = findViewById(R.id.designTestContentMessageButton);
        Comments_Recyclerview = findViewById(R.id.designTestContentCommentRecyclerView);
        ContentCommentEditText = findViewById(R.id.designTestCommentEditText);
        ContentCommentBtn = findViewById(R.id.designTestContentCommentAddButton);
        srtbtn = findViewById(R.id.designTestContentSortingButton);
        BackgroundImageView = findViewById(R.id.designTestViewPagerBackgroundImageView);
        ContentLikeFlicker = findViewById(R.id.desingContentLikeFlicker);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());
        ContentsBtn = findViewById(R.id.ContentActivityContentsButton);
        UserhistoryBtn = findViewById(R.id.ContentActivityUserHistoryButton);
        ChatRoomBtn = findViewById(R.id.ContentActivityChatRoomButtonnDesign);
        ModifyBtn = findViewById(R.id.ContentActivityModifyBtnDesign);

        ContentsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        UserhistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, UserhistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ChatRoomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, ChattingRoomActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.fadein, R.anim.fadeout);
            }
        });

        ModifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, PopUpActivity.class);
                intent.putExtra("ContentID", contentId);
                intent.putExtra("ContentUID", contentUID);
                intent.putExtra("ContentTime", contentTime);
                startActivityForResult(intent, 0);
            }
        });


        contentId = getIntent().getStringExtra("ContentId");
        try {
            byte[] bytes = getIntent().getByteArrayExtra("ImageBitmap");
            Bitmap imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            BackgroundImageView.setImageBitmap(imageBitmap);
        }catch (Exception e){

        }


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); userUID = user.getUid();

        FirestoreManager firestoreManagerForContent = new FirestoreManager(
                ContentActivity.this, "Content", user.getUid());
        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                ContentActivity.this, "UserProfile", user.getUid());
        StorageManager storageManagerForContent = new StorageManager(
                ContentActivity.this, "Image", user.getUid());
        StorageManager storageManagerForUserProfile = new StorageManager(
                ContentActivity.this, "UserProfileImage", user.getUid());

        // todo: for design test
        /* original
        ContentTitleTextView = findViewById(R.id.ContentActivityTitleTextView);
        ContentUserProfileInfoTextView = findViewById(R.id.ContentActivityProfileInfoTextView);
        ContentTimeTextView = findViewById(R.id.ContentActivityTimeTextView);
        ContentDescriptionTextView = findViewById(R.id.ContentActivityDescriptionTextView);
        ContentViewPager = findViewById(R.id.ContentActivityViewPager);
        ContentUserProfileImgImageView = findViewById(R.id.ContentActivityProfileImageImageView);
        ContentMessageBtn = findViewById(R.id.ContentActivityMessageBtn);
        Comments_Recyclerview = findViewById(R.id.comments_recyclerview);
        ContentCommentEditText = findViewById(R.id.ContentActivityCommentEditText);
        ContentCommentBtn = findViewById(R.id.ContentActivityCommentBtn);
        srtbtn = findViewById(R.id.ContentActivitySortingBtn);
         */

        // new one\

        //////////////////////////////////////////////////////////////////////////////////////


        final int[] w = {FLICKERNEGATIVE}; // 나중에 DB에서 값을 받아오게 바꾸3
        ContentLikeFlicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(w[0] == FLICKERNEGATIVE){// 하트가 비어있는 상태에서 버튼을 누르면
                    // DB에 UserLog Write
                    FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                            ContentActivity.this, "UserProfile", user.getUid());
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
                    ContentLikeFlicker.setImageResource(R.drawable.new_heart_red);
                }
                else{                       // 하트가 차있는 상태에서 버튼을 누르면
                    // DB에 UserLog Search & Delete
                    FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                            ContentActivity.this, "UserProfile", user.getUid());
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
                    ContentLikeFlicker.setImageResource(R.drawable.new_heart_empty);
                }

            }
        });

        ContentMessageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String databasePath = "/Messages/" + contentId + "/" + userUID;
                Intent intent = new Intent(ContentActivity.this, ChattingActivity.class);
                intent.putExtra("databasePath", databasePath);
                intent.putExtra("contentId", contentId);
                intent.putExtra("userUID", userUID);
                intent.putExtra("contentUID", contentUID);
                startActivity(intent);
            }
        });


        firestoreManagerForContent.read("Content", contentId, new Callback() {
            @Override
            public void OnCallback(Object object) {
                Content tempContent = (Content)object;

                String uid = tempContent.getUid(); contentUID = uid;
                String title = tempContent.getTitle();
                String description = tempContent.getContent();
                String time = tempContent.getTime(); contentTime = time;

                String year = time.substring(0, 4);
                String month = time.substring(4, 6);
                String day = time.substring(6, 8);
                String hour = time.substring(8, 10);
                String min = time.substring(10, 12);

                ContentTitleTextView.setText(title);
                ContentTimeTextView.setText(year + "." + month + "." + day + " - " + hour + ":" + min);
                ContentDescriptionTextView.setText(description);

                firestoreManagerForUserProfile.read("UserProfile", uid, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        UserProfile tempUserProfile = (UserProfile)object;
                        String nickname = tempUserProfile.getNickName();
                        ContentUserProfileInfoTextView.setText(nickname);

                        // Userlog 읽어오고 특정 엘레멘트가 존재하는지 확인
                        FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(
                                ContentActivity.this, "UserProfile", user.getUid());
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
                                            ContentLikeFlicker.setImageResource(R.drawable.new_heart_red);
                                            w[0] = FLICKERPOSITIVE;
                                            break;
                                        }
                                    }
                                }



                            }
                        });

                        storageManagerForUserProfile.downloadImg2View("UserProfileImage", uid, ContentUserProfileImgImageView, new Callback() {
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
                                                            Intent imgIntent = new Intent(ContentActivity.this, ImageViewActivity.class);
                                                            imgIntent.putExtra("contentId", contentId);
                                                            startActivity(imgIntent);





                                                        }
                                                    });
                                                }
                                            });
                                            adapter.addItem(fragment);
                                            ContentViewPager.setAdapter(adapter);
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


        Comments_Recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));

        List<ExpandableListAdapter.Item> data = new ArrayList<>();
        ExpandableListAdapter expandablelistadapter = new ExpandableListAdapter(data, ContentActivity.this, contentId);
        Comments_Recyclerview.setAdapter(expandablelistadapter);

        String Contentuseruid = contentUID;
        String handphoneuseruid = user.getUid();

        FirestoreManager firestoreManagerForComment = new FirestoreManager(ContentActivity.this, "Comment", user.getUid());

        String temp_path = "Content/" + contentId + "/Comments";

        firestoreManagerForComment.read(temp_path, new Callback() {
            @Override
            public void OnCallback(Object object) {
                ArrayList<Comment> X = (ArrayList<Comment>) object;

                for (Comment QuaryComment : X) {

                    int CommentType;
                    if (QuaryComment.getRecomment_token().equals("0")) {

                        CommentType = ExpandableListAdapter.BACHELOR;

                        data.add(new ExpandableListAdapter.Item(CommentType, QuaryComment.getFrom(), QuaryComment.getTo(), QuaryComment.getTime(), QuaryComment.getMention(), QuaryComment.getRecomment_token()));
                        expandablelistadapter.notifyDataSetChanged();
                    } else {
                        CommentType = ExpandableListAdapter.HEADER;
                        ExpandableListAdapter.Item temp_header = new ExpandableListAdapter.Item(CommentType, QuaryComment.getFrom(), QuaryComment.getTo(), QuaryComment.getTime(), QuaryComment.getMention(), QuaryComment.getRecomment_token());
                        //data.add(temp_header);
                        //final int[] count = {0};

                        // RECOMMENTS 읽어오기
                        firestoreManagerForComment.read(temp_path + "/" + QuaryComment.getTime() + "/Recomments", new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                ArrayList<Comment> Y = (ArrayList<Comment>) object;

                                int Y_length = Y.size();
                                NumberingMachine machine = new NumberingMachine();

                                //data.add(temp_header);
                                for (Comment QuaryRecomment : Y) {
                                    machine.add();
                                    int RecommentType = CHILD;
                                    System.out.println(temp_header.from);
                                    ExpandableListAdapter.Item temp_child = new ExpandableListAdapter.Item(RecommentType, QuaryRecomment.getFrom(), QuaryRecomment.getTo(), QuaryRecomment.getTime(), QuaryRecomment.getMention(), QuaryRecomment.getRecomment_token());
                                    System.out.println(temp_child.from);
                                    temp_header.invisibleChildren.add(temp_child);
                                    /*
                                    if (count[0] == 0) {
                                        data.add(temp_header);
                                        count[0] = 1; }*/

                                    //expandablelistadapter.notifyDataSetChanged();
                                    if(machine.getNumber() == Y_length){
                                        data.add(temp_header);
                                        expandablelistadapter.sorting();
                                        Comments_Recyclerview.setAdapter(expandablelistadapter);

                                    }
                                }
                            }
                        });
                        expandablelistadapter.notifyDataSetChanged();
                    }
                }
            }
        });

        ContentCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp_mention = ContentCommentEditText.getText().toString();
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
                            data.add(new ExpandableListAdapter.Item(ExpandableListAdapter.BACHELOR, user.getUid(), contentUID, temp_comment.getTime(), temp_mention, "0"));
                            expandablelistadapter.notifyDataSetChanged();
                            Comments_Recyclerview.setAdapter(expandablelistadapter);
                        }
                    });
                }
            }
        });


        srtbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandablelistadapter.sorting();
                Comments_Recyclerview.setAdapter(expandablelistadapter);
                // should use notifyitemmoved?

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String contentId = getIntent().getStringExtra("ContentId");
        String temp_path = "Content/" + contentId + "/Comments";
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        List<ExpandableListAdapter.Item> data = new ArrayList<>();
        ExpandableListAdapter expandablelistadapter = new ExpandableListAdapter(data, ContentActivity.this, contentId);
        Comments_Recyclerview.setAdapter(expandablelistadapter);

        FirestoreManager firestoreManagerForComment = new FirestoreManager(ContentActivity.this, "Comment", user.getUid());
        firestoreManagerForComment.read(temp_path, new Callback() {
            @Override
            public void OnCallback(Object object) {
                ArrayList<Comment> X = (ArrayList<Comment>) object;

                for (Comment QuaryComment : X) {

                    int CommentType;
                    if (QuaryComment.getRecomment_token().equals("0")) {

                        CommentType = ExpandableListAdapter.BACHELOR;

                        data.add(new ExpandableListAdapter.Item(CommentType, QuaryComment.getFrom(), QuaryComment.getTo(), QuaryComment.getTime(), QuaryComment.getMention(), QuaryComment.getRecomment_token()));
                        expandablelistadapter.notifyDataSetChanged();
                    } else {
                        CommentType = ExpandableListAdapter.HEADER;
                        ExpandableListAdapter.Item temp_header = new ExpandableListAdapter.Item(CommentType, QuaryComment.getFrom(), QuaryComment.getTo(), QuaryComment.getTime(), QuaryComment.getMention(), QuaryComment.getRecomment_token());
                        //data.add(temp_header);
                        final int[] count = {0};

                        // RECOMMENTS 읽어오기
                        firestoreManagerForComment.read(temp_path + "/" + QuaryComment.getTime() + "/Recomments", new Callback() {
                            @Override
                            public void OnCallback(Object object) {
                                ArrayList<Comment> Y = (ArrayList<Comment>) object;

                                //data.add(temp_header);
                                for (Comment QuaryRecomment : Y) {

                                    int RecommentType = CHILD;

                                    if (count[0] == 0) {
                                        data.add(temp_header);
                                        count[0] = 1;
                                    }
                                    data.add(new ExpandableListAdapter.Item(RecommentType, QuaryRecomment.getFrom(), QuaryRecomment.getTo(), QuaryRecomment.getTime(), QuaryRecomment.getMention(), QuaryRecomment.getRecomment_token()));
                                    expandablelistadapter.notifyDataSetChanged();

                                }
                            }
                        });
                        expandablelistadapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.fadein, R.anim.fadeout);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 0){ // content delete
            if(resultCode == 1){ // content delete done
                setResult(CONTENTDELETEDCODE);
                finish();
            }
        }
    }
}