package com.example.real;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.real.data.Comment;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

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
                                                    String address = "Content/" + contentid +"/Comments/" + header + "/Recomments/" + temp_comment.getTime();
                                                    if(userlog.equals("")){
                                                        // Create Json Obj & JsonArray
                                                        JsonArray frame = new JsonArray();
                                                        JsonObject init = new JsonObject();
                                                        init.addProperty("Type","Comment");
                                                        init.addProperty("Address",address);
                                                        frame.add(init);
                                                        firestoreManagerForUserProfile.update("UserProfile", from, "UserLog",
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
                                                        firestoreManagerForUserProfile.update("UserProfile", from, "UserLog",
                                                                templog.toString(), new Callback() {
                                                                    @Override
                                                                    public void OnCallback(Object object) {

                                                                    }
                                                                });
                                                    }
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