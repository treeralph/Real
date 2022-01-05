package com.example.real;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.real.data.Comment;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;

public class RecommentActivity extends AppCompatActivity {

    Button btn;
    EditText edt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recomment);

        edt = (EditText) findViewById(R.id.recomment_edittext);
        btn = (Button) findViewById(R.id.recomment_button);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String temp_mention = edt.getText().toString();
                if(temp_mention.isEmpty()){}
                else{
                    String contentid = getIntent().getStringExtra("ContentId");
                    String from = getIntent().getStringExtra("From");
                    String to = getIntent().getStringExtra("To");
                    String header = getIntent().getStringExtra("Header");
                    String temp_path = "Content/" + contentid + "/Comments/" + header + "/Recomments";

                    Comment temp_comment = new Comment(from, to, temp_mention, header);

                    FirestoreManager firestoreManagerForComment = new FirestoreManager(RecommentActivity.this, "Comment", from);
                    firestoreManagerForComment.read("Content/" + contentid + "/Comments", header, new Callback() {
                        @Override
                        public void OnCallback(Object object) {
                            Comment temp = (Comment) object;
                            String plus =  String.valueOf(Integer.parseInt(temp.getRecomment_token()) + 1);
                            firestoreManagerForComment.update("Content/" + contentid + "/Comments", header, "Recomment_token", plus, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    firestoreManagerForComment.write(temp_comment, temp_path, temp_comment.getTime(), new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {
                                            FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(RecommentActivity.this, "UserProfile", from);
                                            firestoreManagerForUserProfile.read("UserProfile",from, new Callback() {
                                                @Override
                                                public void OnCallback(Object object) {
                                                    UserProfile userprofile = (UserProfile) object;
                                                    String userlog = userprofile.getUserLog();
                                                    firestoreManagerForUserProfile.update("UserProfile", from, "UserLog",
                                                            userlog + "\n COMMENT WRITE : " + header + " " + temp_comment.toString(), new Callback() {
                                                                @Override
                                                                public void OnCallback(Object object) {
                                                                    finish();
                                                                }
                                                            });
                                                }
                                            });



                                        }
                                    }); };

                            });
                        }
                    });

                }

            }
        });
    }



}