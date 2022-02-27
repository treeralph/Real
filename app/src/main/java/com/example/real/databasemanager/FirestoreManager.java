package com.example.real.databasemanager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.real.Callback;
import com.example.real.data.AuctionContent;
import com.example.real.data.Comment;
import com.example.real.data.Content;
import com.example.real.data.Contents;
import com.example.real.data.Data;
import com.example.real.data.DataLambda;
import com.example.real.data.UserProfile;
import com.firebase.ui.auth.data.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firestore.v1.StructuredQuery;

import java.util.ArrayList;

public class FirestoreManager {

    private final String TAG = "FIRESTOREMANAGER";
    public final int ISSUCCESSFUL = 0;
    public final int ISFAILURE = 1;

    private String dataType;
    private String Uid;
    // dataType = {UserProfile, };

    private FirebaseFirestore db;
    private Context context;

    DataLambda CurrentDataType;

    DataLambda UserProfileType = dict -> {
        UserProfile userProfile = new UserProfile(
                (String)dict.get("rating"),
                (String)dict.get("nickname"),
                (String)dict.get("registertime"),
                (String)dict.get("BO"),
                (String)dict.get("SO"),
                (String)dict.get("Description"),
                (String)dict.get("UserLog"),
                (String)dict.get("DeviceToken"),
                (ArrayList<String>) dict.get("ChattingRoomID"),
                (String)dict.get("Income"),
                (String)dict.get("NumContent"),
                (String)dict.get("Hierarchy"));
        return userProfile;
    };

    DataLambda ContentType = dict -> {
        Content content = new Content(
                (String)dict.get("title"),
                (String)dict.get("content"),
                (String)dict.get("uid"),
                (String)dict.get("time"));
        return content;
    };

    DataLambda AuctionContentType = dict -> {
        AuctionContent auctionContent = new AuctionContent(
                (String)dict.get("title"),
                (String)dict.get("content"),
                (String)dict.get("uid"),
                (String)dict.get("price"),
                (String)dict.get("priceGap"),
                (String)dict.get("auctionDuration"),
                (String)dict.get("auctionState"),
                (ArrayList<String>) dict.get("auctionUserList"),
                (String)dict.get("time"));
        return auctionContent;
    };

    DataLambda ContentsType = dict -> {
        Contents contents = new Contents(
                (String)dict.get("ContentId"),
                (String)dict.get("ContentType"));
        return contents;
    };

    DataLambda CommentType = dict -> {
        Comment comment = new Comment(
                (String)dict.get("From"),
                (String)dict.get("To"),
                (String)dict.get("Mention"),
                (String)dict.get("Time"),
                (String)dict.get("Recomment_token")
        );
        return comment;
    };

    public FirestoreManager(Context context, String dataType, String uid){

        db = FirebaseFirestore.getInstance();

        this.context = context;
        this.dataType = dataType;
        Uid = uid;

        // Type of Data
        if(dataType.equals("UserProfile")){
            CurrentDataType = UserProfileType;
        } else if(dataType.equals("Content")){
            CurrentDataType = ContentType;
        } else if(dataType.equals("AuctionContent")){
            CurrentDataType = AuctionContentType;
        } else if(dataType.equals("Contents")){
            CurrentDataType = ContentsType;
        } else if(dataType.equals("Comment")){
            CurrentDataType = CommentType;
        } else{
            throw new IllegalArgumentException("IllegalArgumentException: " + dataType + " does not exist.");
        }
    }

    /*
    * search method - condition raed
    * read method - path read
    * */


    public void update(String collectionPath, String documentPath, String field, Object fieldData, Callback callback){
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.update(field, fieldData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "is Successful");
                            callback.OnCallback(ISSUCCESSFUL);
                        } else{
                            Log.d(TAG, "Error updating document: ",task.getException());
                            callback.OnCallback(ISFAILURE);
                        }
                    }
                });
    }

    public void search(String path, String field, String data, Callback callback){
        ArrayList<Data> dataList = new ArrayList<>();
        CollectionReference ref = db.collection(path);
        ref.whereEqualTo(field, data).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                dataList.add(CurrentDataType.Constructor(document.getData()));
                            }
                            callback.OnCallback(dataList);
                        } else{
                            Log.d(TAG, "Error getting document: ",task.getException());
                        }
                    }
                });
    }

    public void delete(String collectionPath, String documentPath, Callback callback){
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("Firestore_delete", "DocumentSnapshot successfully deleted!");
                        callback.OnCallback("");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("Firestore_delete", "Error deleting document", e);
                    }
                });
    }


    // "read" Method is overloaded
    public void read(String collectionPath, Callback callback){ // CollectionReference - Read more than one
        ArrayList<Data> dataList = new ArrayList<>();
        CollectionReference ref = db.collection(collectionPath);
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for(QueryDocumentSnapshot document : task.getResult()){
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                dataList.add(CurrentDataType.Constructor(document.getData()));
                            }
                            callback.OnCallback(dataList);
                        } else{
                            Log.d(TAG, "Error getting document: ",task.getException());
                        }
                    }
                });
    }

    public void read(String collectionPath, String documentPath, Callback callback){ // DocumentReference - only Read one
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Data datum = CurrentDataType.Constructor(document.getData());
                            callback.OnCallback(datum);
                        } else{
                            Log.d(TAG, "Error getting document: ",task.getException());
                        }
                    }
                });
    }

    public void read(String collectionPath, String documentPath, Callback successCallback, Callback failureCallback){ // DocumentReference - only Read one
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            if (document.exists()) {
                                Data datum = CurrentDataType.Constructor(document.getData());
                                successCallback.OnCallback(datum);
                            }else{
                                failureCallback.OnCallback(null);
                            }
                        } else{
                            Log.d(TAG, "Error getting document: ",task.getException());
                        }
                    }
                });
    }

    public void readIgnore(String collectionPath, String documentPath, Callback callback){ // DocumentReference - only Read one
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            try {
                                Data datum = CurrentDataType.Constructor(document.getData());
                                callback.OnCallback(datum);
                            } catch(Exception e){
                                e.printStackTrace();
                                callback.OnCallback(null);
                            }
                        } else{
                            Log.d(TAG, "Error getting document: ",task.getException());
                        }
                    }
                });
    }


    // write method is overloaded
    public void write(Data datum, String path, Callback callback){
        CollectionReference ref = db.collection(path);
        ref.add(datum.DataOut())
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, task.getResult().getId() + " write Success");
                            callback.OnCallback(task.getResult().getId());
                        } else{
                            Log.d(TAG, "Error getting document: ",task.getException());
                        }
                    }
                });
    }

    public void write(Data datum, String collectionPath, String documentPath, Callback callback){
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.set(datum.DataOut())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, task.getResult() + " write Success");
                            callback.OnCallback(documentPath);
                        } else{
                            Log.d(TAG, "Error getting document: ",task.getException());
                        }
                    }
        });
    }

    public void transactionUpdate(String newFieldData, String collectionPath, String documentPath, String fieldPath, Callback callback1, Callback callback2){

        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        db.runTransaction(new Transaction.Function<Object>() {
            @Override
            public Object apply(Transaction transaction) throws FirebaseFirestoreException {
                transaction.update(ref, fieldPath, newFieldData);
                return "박강혁";
            }
        }).addOnSuccessListener(new OnSuccessListener<Object>() {
            @Override
            public void onSuccess(Object o) {
                Log.d("Firebase:transactionUpdate", "Success");
                callback1.OnCallback(o);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("Firebase:transactionUpdate", "Failure");
                callback2.OnCallback(null);
            }
        });
    }
}
