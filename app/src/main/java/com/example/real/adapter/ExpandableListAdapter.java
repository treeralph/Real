package com.example.real.adapter;


import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.real.Callback;
import com.example.real.ContentActivity;
import com.example.real.PeekUserProfileActivity;
import com.example.real.R;
import com.example.real.RecommentActivity;
import com.example.real.data.Comment;
import com.example.real.data.UserProfile;
import com.example.real.databasemanager.FirestoreManager;
import com.example.real.databasemanager.StorageManager;
import com.example.real.tool.NumberingMachine;
import com.example.real.tool.TimeTextTool;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExpandableListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int HEADER = 0;
    public static final int CHILD = 1;
    public static final int BACHELOR = 2;
    //public static final int COMMENT = 3;

    private List<Item> data;
    private Context context;
    private String contentid;
    FirebaseUser user;


    public ExpandableListAdapter(List<Item> data) {
        this.data = data;
    }

    public ExpandableListAdapter(List<Item> data, Context context, String contentid) {
        this.data = data;
        this.context = context;
        this.contentid = contentid;
    }



    // XML ??????
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int type) {
        View view = null;
        Context context = parent.getContext();

        switch (type) {
            case HEADER:
                LayoutInflater inflaterHeader = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflaterHeader.inflate(R.layout.comment_header_item, parent, false);
                ListHeaderViewHolder header = new ListHeaderViewHolder(view);
                return header;
            case CHILD:
                LayoutInflater inflaterChild = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflaterChild.inflate(R.layout.comment_child_item, parent, false);
                ListChildViewHolder child = new ListChildViewHolder(view);
                return child;
            case BACHELOR:
                LayoutInflater inflaterBACHELOR = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                // todo: for design test
                //view = inflaterBACHELOR.inflate(R.layout.comment_bachelor_item, parent, false);
                view = inflaterBACHELOR.inflate(R.layout.comment_item_design, parent, false);
                /////////////////////
                ListBachelorViewHolder bachelor = new ListBachelorViewHolder(view);
                return bachelor;
            /*
            case COMMENT:
                LayoutInflater inflaterCOMMENT = (LayoutInflater) parent.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflaterCOMMENT.inflate(R.layout.list_comment, parent, false);
                ListCommentViewHolder comment = new ListCommentViewHolder(view);
                return comment;*/

        }
        return null;
    };


    // ???????????? & ?????? ??????
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final Item item = data.get(position);

        user = FirebaseAuth.getInstance().getCurrentUser();
        StorageManager storageManagerForUserProfileImage = new StorageManager(context, "UserProfileImage", user.getUid());


        switch (item.type) {
            case HEADER:

                final ListHeaderViewHolder itemController = (ListHeaderViewHolder) holder;
                itemController.refferalItem = item;
                itemController.header_recommentview.setVisibility(View.GONE);
                FirestoreManager firestoreManagerforUserprofile1 = new FirestoreManager(context, "UserProfile",item.from);
                firestoreManagerforUserprofile1.read("UserProfile", item.from, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        UserProfile header = (UserProfile) object;
                        itemController.header_nickname.setText(header.getNickName());
                    }
                });
                itemController.header_nickname.setText(item.from);

                storageManagerForUserProfileImage.downloadImg2View("UserProfileImage", item.from, itemController.header_profile_image, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        itemController.header_profile_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(context, PeekUserProfileActivity.class);
                                intent.putExtra("userProfileUID", item.from);
                                //intent.putExtra("userProfileImageByteArray", byteArray);

                                Pair[] pairs = new Pair[1];
                                try{
                                    pairs[0] = new Pair<View, String>(((Activity) (context)).findViewById(R.id.ContentActivityTransitionView), "testView");

                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                                    context.startActivity(intent, options.toBundle());
                                    ((Activity) (context)).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                }catch (Exception e){
                                    pairs[0] = new Pair<View, String>(((Activity) (context)).findViewById(R.id.AuctionContentActivityTransitionView), "testView");

                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                                    context.startActivity(intent, options.toBundle());
                                    ((Activity) (context)).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                }
                            }
                        });
                    }});

                String asdfgh = new TimeTextTool(item.time).Time2Text();
                itemController.header_time.setText(asdfgh);
                itemController.header_mention.setText(item.mention);




                itemController.btn_expand_toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (item.invisibleChildren == null) {
                            item.invisibleChildren = new ArrayList<Item>();
                            int count = 0;
                            int pos = data.indexOf(itemController.refferalItem);
                            while (data.size() > pos + 1 && data.get(pos + 1).type == CHILD) {
                                item.invisibleChildren.add(data.remove(pos + 1));
                                count++;
                            }
                            notifyItemRangeRemoved(pos + 1, count);
                            itemController.btn_expand_toggle.setText("?????? "+ item.recomment_token+ "??? ??????");//down
                        } else {
                            int pos = data.indexOf(itemController.refferalItem);
                            int index = pos + 1;
                            for (Item i : item.invisibleChildren) {
                                data.add(index, i);
                                index++;
                            }
                            notifyItemRangeInserted(pos + 1, index - pos - 1);
                            itemController.btn_expand_toggle.setText("?????? "+ item.recomment_token+ "??? ?????????");//up
                            item.invisibleChildren = null;
                        }
                    }
                });

                if (item.invisibleChildren == null) {

                    itemController.btn_expand_toggle.setText("?????? "+ item.recomment_token+ "??? ?????????");//up
                } else {
                    itemController.btn_expand_toggle.setText("?????? "+ item.recomment_token+ "??? ??????");//down
                }

                /*
                itemController.header_linearlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentX = new Intent(context, RecommentActivity.class);

                        user = FirebaseAuth.getInstance().getCurrentUser();
                        intentX.putExtra("ContentId", contentid);
                        intentX.putExtra("From", user.getUid());
                        intentX.putExtra("To", item.from);
                        intentX.putExtra("Header", item.time);
                        context.startActivity(intentX);
                    }
                });

                 */

                itemController.header_linearlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(itemController.header_recommentview.getVisibility()==View.GONE){
                            itemController.header_recommentview.setVisibility(View.VISIBLE);
                        }else{
                            itemController.header_recommentview.setVisibility(View.GONE);
                        }
                    }
                });

                itemController.header_recommentbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String temp_mention = itemController.header_recommentedittext.getText().toString();
                        if(temp_mention.isEmpty()){}
                        else{
                            itemController.header_recommentview.setVisibility(View.GONE);
                            // String contentid = contentid;
                            String from = user.getUid();
                            String to = item.from;
                            String header = item.time;
                            String temp_path = "Content/" + contentid + "/Comments/" + header + "/Recomments";
                            Comment temp_comment = new Comment(from, to, temp_mention, header);
                            FirestoreManager firestoreManagerForComment = new FirestoreManager(context, "Comment", from);
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
                                                    refresh(contentid);
                                                    FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(context, "UserProfile", from);
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








                itemController.header_popup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(user.getUid().equals(item.from)){
                            PopupMenu popup1 = new PopupMenu(context, v);
                            popup1.getMenuInflater().inflate(R.menu.commentpopup1,popup1.getMenu());
                            popup1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem mitem) {
                                    if(mitem.getItemId() == R.id.commentpopup1){
                                        FirestoreManager firestoreManagerforcomment = new FirestoreManager(context, "Comment",user.getUid());
                                        firestoreManagerforcomment.update("Content/" + contentid + "/Comments", item.time, "Mention", "????????? ???????????????.", new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {
                                                popup1.dismiss();
                                            }
                                        });
                                    }
                                    return false;
                                }
                            });
                            popup1.show();
                        }



                    }
                });
                break;
            case CHILD:
                final ListChildViewHolder itemController1 = (ListChildViewHolder) holder;
                itemController1.refferalItem = item;
                //itemController.header_profile_image.setImageURI(item.);
                itemController1.child_recommentview.setVisibility(View.GONE);
                FirestoreManager firestoreManagerforUserprofile = new FirestoreManager(context, "UserProfile",item.from);
                firestoreManagerforUserprofile.read("UserProfile", item.from, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        UserProfile x = (UserProfile) object ;
                        itemController1.child_nickname.setText(x.getNickName());
                    }
                });
                storageManagerForUserProfileImage.downloadImg2View("UserProfileImage", item.from, itemController1.child_profile_image, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        itemController1.child_profile_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                Intent intent = new Intent(context, PeekUserProfileActivity.class);
                                intent.putExtra("userProfileUID", item.from);
                                //intent.putExtra("userProfileImageByteArray", byteArray);

                                Pair[] pairs = new Pair[1];
                                try{
                                    pairs[0] = new Pair<View, String>(((Activity) (context)).findViewById(R.id.ContentActivityTransitionView), "testView");

                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                                    context.startActivity(intent, options.toBundle());
                                    ((Activity) (context)).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                }catch (Exception e){
                                    pairs[0] = new Pair<View, String>(((Activity) (context)).findViewById(R.id.AuctionContentActivityTransitionView), "testView");

                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                                    context.startActivity(intent, options.toBundle());
                                    ((Activity) (context)).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                }
                            }
                        });
                    }});

                String asdfg = new TimeTextTool(item.time).Time2Text();
                itemController1.child_time.setText(asdfg);


                FirestoreManager firestoreManagerforHeader = new FirestoreManager(context, "Comment", user.getUid());
                firestoreManagerforHeader.read("Content/" + contentid + "/Comments" , item.recomment_token, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        Comment Header = (Comment) object;
                        if(Header.getFrom().equals(item.to)){itemController1.child_mention.setText(item.mention);}
                        else{
                            firestoreManagerforUserprofile.read("UserProfile", item.to, new Callback() {
                                @Override
                                public void OnCallback(Object object) {
                                    firestoreManagerforUserprofile.read("UserProfile", item.to, new Callback() {
                                        @Override
                                        public void OnCallback(Object object) {
                                            UserProfile itemto = (UserProfile) object;
                                            itemController1.child_toanother.setText("@" + itemto.getNickName());
                                            itemController1.child_mention.setText( item.mention);
                                        }
                                    });

                                }
                            });

                        }
                    }
                });

                /*
                itemController1.child_linearlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentX = new Intent(context, RecommentActivity.class);

                        user = FirebaseAuth.getInstance().getCurrentUser();
                        intentX.putExtra("ContentId", contentid);
                        intentX.putExtra("From", user.getUid());
                        intentX.putExtra("To", item.from);

                        int parameter = position;
                        while (data.get(parameter).type == CHILD){
                            parameter -= 1;
                        };
                        intentX.putExtra("Header", data.get(parameter).time );
                        context.startActivity(intentX);
                    }
                });
                 */

                itemController1.child_linearlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(itemController1.child_recommentview.getVisibility()==View.GONE){
                            itemController1.child_recommentview.setVisibility(View.VISIBLE);
                        }else{
                            itemController1.child_recommentview.setVisibility(View.GONE);
                        }
                    }
                });

                itemController1.child_recommentbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String temp_mention = itemController1.child_recommentedittext.getText().toString();
                        if(temp_mention.isEmpty()){}
                        else{
                            itemController1.child_recommentview.setVisibility(View.GONE);
                            // String contentid = contentid;
                            String from = user.getUid();
                            String to = item.from;
                            int parameter = position;
                            while (data.get(parameter).type == CHILD){
                                parameter -= 1;
                            };
                            String header = data.get(parameter).time;
                            String temp_path = "Content/" + contentid + "/Comments/" + header + "/Recomments";
                            Comment temp_comment = new Comment(from, to, temp_mention, header);
                            FirestoreManager firestoreManagerForComment = new FirestoreManager(context, "Comment", from);
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
                                                    refresh(contentid);
                                                    FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(context, "UserProfile", from);
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










                itemController1.child_popup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(user.getUid().equals(item.from)){
                            PopupMenu popup1 = new PopupMenu(context, v);
                            popup1.getMenuInflater().inflate(R.menu.commentpopup1,popup1.getMenu());
                            popup1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem mitem) {
                                    if(mitem.getItemId() == R.id.commentpopup1){
                                        FirestoreManager firestoreManagerforcomment = new FirestoreManager(context, "Comment",user.getUid());
                                        firestoreManagerforcomment.update("Content/" + contentid + "/Comments/" + item.recomment_token + "/Recomments", item.time, "Mention", "????????? ???????????????.", new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {
                                                popup1.dismiss();
                                            }
                                        });
                                    }
                                    return false;
                                }
                            });
                            popup1.show();
                        }



                    }
                });
                /*
                itemController1.btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(view.getContext(), item.text,Toast.LENGTH_SHORT).show();
                    }
                });*/


                break;
            case BACHELOR:
                final ListBachelorViewHolder itemController2 = (ListBachelorViewHolder) holder;
                itemController2.refferalItem = item;

                FirestoreManager firestoreManagerforUserprofile2 = new FirestoreManager(context, "UserProfile", item.from);


                itemController2.bachelor_recommentview.setVisibility(View.GONE);


                firestoreManagerforUserprofile2.read("UserProfile", item.from, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        UserProfile bachelor = (UserProfile) object;
                        itemController2.bachelor_nickname.setText(bachelor.getNickName());
                    }
                });
                storageManagerForUserProfileImage.downloadImg2View("UserProfileImage", item.from, itemController2.bachelor_profile_image, new Callback() {
                    @Override
                    public void OnCallback(Object object) {
                        itemController2.bachelor_profile_image.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(context, PeekUserProfileActivity.class);
                                intent.putExtra("userProfileUID", item.from);
                                //intent.putExtra("userProfileImageByteArray", byteArray);

                                Pair[] pairs = new Pair[1];
                                try{
                                    pairs[0] = new Pair<View, String>(((Activity) (context)).findViewById(R.id.ContentActivityTransitionView), "testView");

                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                                    context.startActivity(intent, options.toBundle());
                                    ((Activity) (context)).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                }catch (Exception e){
                                    pairs[0] = new Pair<View, String>(((Activity) (context)).findViewById(R.id.AuctionContentActivityTransitionView), "testView");

                                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context, pairs);
                                    context.startActivity(intent, options.toBundle());
                                    ((Activity) (context)).overridePendingTransition(R.anim.fadein, R.anim.fadeout);
                                }
                            }
                        });
                    }});

                String asdf = new TimeTextTool(item.time).Time2Text();
                itemController2.bachelor_time.setText(asdf);


                itemController2.bachelor_mention.setText(item.mention);
                /*
                itemController2.bachelor_linearlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentX = new Intent(context, RecommentActivity.class);

                        user = FirebaseAuth.getInstance().getCurrentUser();
                        intentX.putExtra("ContentId", contentid);
                        intentX.putExtra("From", user.getUid());
                        intentX.putExtra("To", item.from);
                        intentX.putExtra("Header", item.time);
                        context.startActivity(intentX);

                        //Toast.makeText(context,"asdf",Toast.LENGTH_SHORT).show();
                    }
                });
                 */

                itemController2.bachelor_linearlayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(itemController2.bachelor_recommentview.getVisibility()==View.GONE){
                            itemController2.bachelor_recommentview.setVisibility(View.VISIBLE);
                        }else{
                            itemController2.bachelor_recommentview.setVisibility(View.GONE);
                        }
                    }
                });
                itemController2.bachelor_recommentbtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String temp_mention = itemController2.bachelor_recommentedittext.getText().toString();
                        if(temp_mention.isEmpty()){}
                        else{
                            itemController2.bachelor_recommentview.setVisibility(View.GONE);
                            // String contentid = contentid;
                            String from = user.getUid();
                            String to = item.from;
                            String header = item.time;
                            String temp_path = "Content/" + contentid + "/Comments/" + header + "/Recomments";
                            Comment temp_comment = new Comment(from, to, temp_mention, header);
                            FirestoreManager firestoreManagerForComment = new FirestoreManager(context, "Comment", from);
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
                                                    //data.add(position+1,new ExpandableListAdapter.Item(ExpandableListAdapter.CHILD, from, to, header, temp_mention, "0"));
                                                    //notifyItemInserted(position+1);
                                                    refresh(contentid);
                                                    FirestoreManager firestoreManagerForUserProfile = new FirestoreManager(context, "UserProfile", from);
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


                itemController2.bachelor_popup.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(user.getUid().equals(item.from)){
                            PopupMenu popup1 = new PopupMenu(context, v);
                            popup1.getMenuInflater().inflate(R.menu.commentpopup1,popup1.getMenu());
                            popup1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem mitem) {
                                    if(mitem.getItemId() == R.id.commentpopup1){
                                        FirestoreManager firestoreManagerforcomment = new FirestoreManager(context, "Comment",user.getUid());
                                        firestoreManagerforcomment.update("Content/" + contentid + "/Comments", item.time, "Mention", "????????? ???????????????.", new Callback() {
                                            @Override
                                            public void OnCallback(Object object) {
                                                popup1.dismiss();
                                            }
                                        });
                                    }
                                    return false;
                                }
                            });
                            popup1.show();
                        }



                    }
                });
                break;


            /*
            case COMMENT:
                final ListCommentViewHolder itemController3 = (ListCommentViewHolder) holder;
                itemController3.refferalItem = item;
                itemController3.comment_from.setText(item.from);
                itemController3.comment_to.setText(item.to);
                itemController3.comment_time.setText(item.time);
                itemController3.comment_comment.setText(item.comment);
                break;*/
        }
    }

    public void sorting(){
        ArrayList<Item> HeaderBachelor = new ArrayList<>();
        ArrayList<Item> Child = new ArrayList<>();

        for ( Item temp : data){
            if (temp.type == CHILD ) { Child.add(temp);}
            else {HeaderBachelor.add(temp);}
        }
        Comparator<Item> itemComparator = new Comparator<Item>() {
            @Override
            public int compare(Item o1, Item o2) {
                return (int) (Long.parseLong(o2.time) - Long.parseLong(o1.time));
            }
        };
        HeaderBachelor.sort(itemComparator);

        //System.out.println("child " + Child.toString() );
        //System.out.println("parent " + HeaderBachelor.toString());
        ArrayList<Item> sorted_array = new ArrayList<>();
        for ( Item parent : HeaderBachelor){
            sorted_array.add(parent);
            //System.out.println(parent.mention);
            for ( Item children : Child){
                if(children.recomment_token.equals(parent.time)){
                    sorted_array.add(children);
                    //System.out.println(children.mention);
                }else{
                    //System.out.println(children.recomment_token + "versus" + parent.time);
                }
            }
        }
        System.out.println("sorted array" + sorted_array.toString());

        //final CommentDiffUtil diffutil = new CommentDiffUtil(data,sorted_array);
        //final DiffUtil.DiffResult diffresult = DiffUtil.calculateDiff(diffutil);
        data.clear();
        data.addAll(sorted_array);
        notifyDataSetChanged();
        //diffresult.dispatchUpdatesTo();

    }

    public void refresh(String contentid){
        ArrayList<Item> refreshed_item = new ArrayList<>();

        FirestoreManager firestoreManager = new FirestoreManager( context, "Comment", user.getUid());
        firestoreManager.read("Content/" + contentid + "/Comments", new Callback() {
            @Override
            public void OnCallback(Object object) {
                ArrayList<Comment> X = (ArrayList<Comment>) object;
                for (Comment QuaryComment : X) {

                    int CommentType;
                    if (QuaryComment.getRecomment_token().equals("0")) {

                        CommentType = ExpandableListAdapter.BACHELOR;

                        refreshed_item.add(new ExpandableListAdapter.Item(CommentType, QuaryComment.getFrom(), QuaryComment.getTo(), QuaryComment.getTime(), QuaryComment.getMention(), QuaryComment.getRecomment_token()));
                        data.clear();
                        data.addAll(refreshed_item);
                        notifyDataSetChanged();
                    } else {
                        CommentType = ExpandableListAdapter.HEADER;
                        ExpandableListAdapter.Item temp_header = new ExpandableListAdapter.Item(CommentType, QuaryComment.getFrom(), QuaryComment.getTo(), QuaryComment.getTime(), QuaryComment.getMention(), QuaryComment.getRecomment_token());
                        //data.add(temp_header);
                        //final int[] count = {0};

                        // RECOMMENTS ????????????
                        firestoreManager.read("Content/" + contentid + "/Comments/" + QuaryComment.getTime() + "/Recomments", new Callback() {
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
                                        refreshed_item.add(temp_header);
                                        sorting();
                                        data.clear();
                                        data.addAll(refreshed_item);
                                        notifyDataSetChanged();

                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return data.get(position).type;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }


    // ??? ?????? ????????? ????????? ??????
    private static class ListHeaderViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout header_linearlayout;
        public ImageView header_profile_image;
        public TextView header_nickname;
        public TextView header_time;
        public TextView header_mention;
        public TextView btn_expand_toggle;
        public ImageView header_popup;
        public Item refferalItem;
        public CardView header_recommentview;
        public EditText header_recommentedittext;
        public Button header_recommentbtn;

        public ListHeaderViewHolder(View itemView) {
            super(itemView);
            header_linearlayout = (LinearLayout) itemView.findViewById(R.id.header_linearlayout);
            header_profile_image = (ImageView) itemView.findViewById(R.id.header_profile_img);
            header_nickname = (TextView) itemView.findViewById(R.id.header_nickname);
            header_time = (TextView) itemView.findViewById(R.id.header_time);
            header_mention = (TextView) itemView.findViewById(R.id.header_mention);
            header_popup = (ImageView) itemView.findViewById(R.id.header_popup);
            btn_expand_toggle = (TextView) itemView.findViewById(R.id.header_toggle);
            header_recommentview = (CardView) itemView.findViewById(R.id.header_recommentview);
            header_recommentedittext = (EditText) itemView.findViewById(R.id.header_recommentedittext);
            header_recommentbtn = (Button) itemView.findViewById(R.id.header_recommentbtn);
        }
    }
    private static class ListChildViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout child_linearlayout;
        public ImageView child_profile_image;
        public TextView child_nickname;
        public TextView child_time;
        public TextView child_mention;
        public Item refferalItem;
        public TextView child_toanother;
        public ImageView child_popup;
        public CardView child_recommentview;
        public EditText child_recommentedittext;
        public Button child_recommentbtn;
        public ListChildViewHolder(View itemView) {
            super(itemView);
            child_linearlayout = (LinearLayout) itemView.findViewById(R.id.child_linearlayout);
            child_profile_image = (ImageView) itemView.findViewById(R.id.child_profile_img);
            child_nickname = (TextView) itemView.findViewById(R.id.child_nickname);
            child_time = (TextView) itemView.findViewById(R.id.child_time);
            child_mention = (TextView) itemView.findViewById(R.id.child_mention);
            child_popup = (ImageView) itemView.findViewById(R.id.child_popup);
            child_toanother = (TextView) itemView.findViewById(R.id.child_toanother);
            child_recommentview = (CardView) itemView.findViewById(R.id.child_recommentview);
            child_recommentedittext = (EditText) itemView.findViewById(R.id.child_recommentedittext);
            child_recommentbtn = (Button) itemView.findViewById(R.id.child_recommentbtn);
        }
    }
    private static class ListBachelorViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout bachelor_linearlayout;
        public ImageView bachelor_profile_image;
        public TextView bachelor_nickname;
        public TextView bachelor_time;
        public TextView bachelor_mention;
        public ImageView bachelor_popup;
        public Item refferalItem;
        public CardView bachelor_recommentview;
        public EditText bachelor_recommentedittext;
        public Button bachelor_recommentbtn;

        public ListBachelorViewHolder(View itemView) {
            super(itemView);


            /* todo: for design test
            bachelor_linearlayout = (LinearLayout) itemView.findViewById(R.id.bachelor_linearlayout);
            bachelor_profile_image = (ImageView) itemView.findViewById(R.id.bachelor_profile_img);
            bachelor_nickname = (TextView) itemView.findViewById(R.id.bachelor_nickname);
            bachelor_time = (TextView) itemView.findViewById(R.id.bachelor_time);
            bachelor_popup = (ImageView) itemView.findViewById(R.id.bachelor_popup);
            bachelor_mention = (TextView) itemView.findViewById(R.id.bachelor_mention);
             */

            bachelor_linearlayout = (LinearLayout) itemView.findViewById(R.id.designTestCommentLinearLayout);
            bachelor_profile_image = (ImageView) itemView.findViewById(R.id.designTestCommentUserProfileImageImageView);
            bachelor_nickname = (TextView) itemView.findViewById(R.id.designTestCommentUserProfileNickNameTextView);
            bachelor_time = (TextView) itemView.findViewById(R.id.designTestCommentTimeTextView);
            bachelor_popup = (ImageView) itemView.findViewById(R.id.designTestCommentPopUpButton);
            bachelor_mention = (TextView) itemView.findViewById(R.id.designTestCommentDescriptionTextView);
            bachelor_recommentview = (CardView) itemView.findViewById(R.id.bachelor_recommentview);
            bachelor_recommentedittext = (EditText) itemView.findViewById(R.id.bachelor_recommentedittext);
            bachelor_recommentbtn = (Button) itemView.findViewById(R.id.bachelor_recommentbtn);

        }
    }
    /*
    private static class ListCommentViewHolder extends RecyclerView.ViewHolder {
        public TextView comment_from;
        public TextView comment_to;
        public TextView comment_time;
        public TextView comment_comment;
        public Item refferalItem;

        public ListCommentViewHolder(View itemView) {
            super(itemView);
            comment_from = (TextView) itemView.findViewById(R.id.comment_From);
            comment_to = (TextView) itemView.findViewById(R.id.comment_To);
            comment_time = (TextView) itemView.findViewById(R.id.comment_Time);
            comment_comment = (TextView) itemView.findViewById(R.id.comment_Comment);
        }
    }*/

    public static class Item implements Comparable<Item> {
        public int type;
        public String text;
        public List<Item> invisibleChildren;
        public String from;
        public String to;
        public String time;
        public String mention;
        public String recomment_token;


        public Item() {
        }

        public Item(int type, String text) {
            this.type = type;
            this.text = text;
        }

        public Item(int type, String from, String to, String time, String mention, String Recomment_token){
            this.type = type;
            this.from = from;
            this.to = to;
            this.time = time;
            this.mention = mention;
            this.recomment_token = Recomment_token;
            this.invisibleChildren = new ArrayList<>();

        }


        @Override
        public int compareTo(Item o) {
            if(Integer.parseInt(this.time) > Integer.parseInt(o.time)){return 1;}
            else if(this.time == o.time){return 0;}
            else{return -1;}

        }



        @Override
        public String toString() {
            return "Item{" +
                    "type=" + type +
                    ", text='" + text + '\'' +
                    ", invisibleChildren=" + invisibleChildren +
                    ", from='" + from + '\'' +
                    ", to='" + to + '\'' +
                    ", time='" + time + '\'' +
                    ", mention='" + mention + '\'' +
                    "Recomment_token='" + recomment_token + '\'' +
                    '}';
        }
    }


}











