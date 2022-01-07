package com.example.real.databasemanager;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.real.Callback;
import com.example.real.data.Message;
import com.example.real.data.MessageTransmission;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class RealTimeDatabaseManager {

    private final String TAG = "REALTIMEDATABASEMANAGER";
    private final String STORAGE_LOCATION = "https://real-95e0b-default-rtdb.asia-southeast1.firebasedatabase.app/";

    private Context context;
    private String rootPath;
    private String uid;

    private FirebaseDatabase db;

    public RealTimeDatabaseManager(Context context, String rootPath, String uid) {

        this.context = context;
        this.rootPath = rootPath;
        this.uid = uid;

        db = FirebaseDatabase.getInstance(STORAGE_LOCATION);
    }


    public void readMessage(String path, Callback callback){

        DatabaseReference ref = db.getReference(path);
        ref.get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                ArrayList<Message> messageList = new ArrayList<>();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Log.d(TAG+"/readMessage", snapshot.toString());
                    DataSnapshot tempSnapshot = snapshot.getChildren().iterator().next();
                    Message tempMessage = tempSnapshot.getValue(Message.class);
                    messageList.add(tempMessage);
                }
                callback.OnCallback(messageList);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void readMessageLooper(String path, Callback callback){

        DatabaseReference ref = db.getReference(path);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Log.d(TAG, snapshot.toString());
                    DataSnapshot tempSnapshot = snapshot.getChildren().iterator().next();
                    Message tempMessage = tempSnapshot.getValue(Message.class);
                    callback.OnCallback(tempMessage);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void readMessageLooper(String path, String subPath, Callback callback){

        DatabaseReference ref = db.getReference(rootPath + "/" + path + "/" + subPath);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    Log.d(TAG, snapshot.toString());
                    DataSnapshot tempSnapshot = snapshot.getChildren().iterator().next();
                    Message tempMessage = tempSnapshot.getValue(Message.class);
                    callback.OnCallback(tempMessage);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void writeMessage(String path, String subPath, Message message){

        DatabaseReference ref = db.getReference(rootPath + "/" + path + "/" + subPath + "/" + message.getTime() + "/Message");
        ref.setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DATA_SETVALUE_IS_COMPLETE");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        });
    }
    public void writeMessage(String path, Message message){ //

        DatabaseReference ref = db.getReference(path + "/" + message.getTime() + "/Message");
        ref.setValue(message).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "DATA_SETVALUE_IS_COMPLETE");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, e.getMessage());
                e.printStackTrace();
            }
        });
    }



}
