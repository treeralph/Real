package com.example.real.databasemanager;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.real.AuctionContentActivity;
import com.example.real.Callback;
import com.example.real.Callback2;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firestore.v1.Document;
import com.google.firestore.v1.StructuredQuery;
import com.google.firestore.v1.TransactionOptions;
import com.google.firestore.v1.TransactionOptionsOrBuilder;
import com.google.protobuf.MessageLite;
import com.naver.maps.geometry.LatLng;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                (String) dict.get("rating"),
                (String) dict.get("nickname"),
                (String) dict.get("registertime"),
                (String) dict.get("BO"),
                (String) dict.get("SO"),
                (String) dict.get("Description"),
                (String) dict.get("UserLog"),
                (String) dict.get("DeviceToken"),
                (ArrayList<String>) dict.get("ChattingRoomID"),
                (String) dict.get("Income"),
                (String) dict.get("NumContent"),
                (String) dict.get("Hierarchy"));
        return userProfile;
    };

    DataLambda ContentType = dict -> {
        Content content = new Content(
                (String) dict.get("title"),
                (String) dict.get("content"),
                (String) dict.get("uid"),
                (String) dict.get("time"),
                (String) dict.get("category"),
                (String) dict.get("location"),
                (String) dict.get("price"),
                (String) dict.get("latLng"),
                (String) dict.get("adm_cd"));
        return content;
    };

    DataLambda AuctionContentType = dict -> {
        AuctionContent auctionContent = new AuctionContent(
                (String) dict.get("title"),
                (String) dict.get("content"),
                (String) dict.get("uid"),
                (String) dict.get("price"),
                (String) dict.get("priceGap"),
                (String) dict.get("auctionDuration"),
                (String) dict.get("auctionState"),
                (ArrayList<String>) dict.get("auctionUserList"),
                (String) dict.get("time"),
                (String) dict.get("auctionEndTime"),
                (String) dict.get("category"),
                (String) dict.get("location"),
                (String) dict.get("latLng"),
                (String) dict.get("adm_cd"));
        return auctionContent;
    };

    DataLambda ContentsType = dict -> {
        Contents contents = new Contents(
                (String) dict.get("ContentId"),
                (String) dict.get("ContentType"),
                (String) dict.get("ContentTitle"),
                (String) dict.get("Category"),
                (ArrayList<String>) dict.get("WordCase"),
                (String) dict.get("latLng"),
                (String) dict.get("time"),
                (String) dict.get("adm_cd"),
                (String) dict.get("auctionState"));
        return contents;
    };

    DataLambda CommentType = dict -> {
        Comment comment = new Comment(
                (String) dict.get("From"),
                (String) dict.get("To"),
                (String) dict.get("Mention"),
                (String) dict.get("Time"),
                (String) dict.get("Recomment_token")
        );
        return comment;
    };

    public FirestoreManager(Context context, String dataType, String uid) {

        db = FirebaseFirestore.getInstance();

        this.context = context;
        this.dataType = dataType;
        Uid = uid;

        // Type of Data
        if (dataType.equals("UserProfile")) {
            CurrentDataType = UserProfileType;
        } else if (dataType.equals("Content")) {
            CurrentDataType = ContentType;
        } else if (dataType.equals("AuctionContent")) {
            CurrentDataType = AuctionContentType;
        } else if (dataType.equals("Contents")) {
            CurrentDataType = ContentsType;
        } else if (dataType.equals("Comment")) {
            CurrentDataType = CommentType;
        } else {
            throw new IllegalArgumentException("IllegalArgumentException: " + dataType + " does not exist.");
        }
    }

    /*
     * search method - condition raed
     * read method - path read
     * */


    public void update(String collectionPath, String documentPath, String field, Object fieldData, Callback callback) {
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.update(field, fieldData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "is Successful");
                            callback.OnCallback(ISSUCCESSFUL);
                        } else {
                            Log.d(TAG, "Error updating document: ", task.getException());
                            callback.OnCallback(ISFAILURE);
                        }
                    }
                });
    }

    public void search(String path, String field, String data, Callback callback) {
        ArrayList<Data> dataList = new ArrayList<>();
        CollectionReference ref = db.collection(path);
        ref.whereEqualTo(field, data).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                dataList.add(CurrentDataType.Constructor(document.getData()));
                            }
                            callback.OnCallback(dataList);
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    public void searchIn(String path, String field, ArrayList<String> queryList, Callback callback) {
        ArrayList<Data> dataList = new ArrayList<>();
        CollectionReference ref = db.collection(path);
        ref.whereArrayContainsAny(field, queryList).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, "searchIn. " + document.getId() + " => " + document.getData());
                                dataList.add(CurrentDataType.Constructor(document.getData()));
                            }
                            callback.OnCallback(dataList);
                        } else {
                            Log.d(TAG, "searchIn. Error getting document: ", task.getException());
                        }
                    }
                });
    }

    public void delete(String collectionPath, String documentPath, Callback callback) {
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
    public void read(String collectionPath, Callback callback) { // CollectionReference - Read more than one
        ArrayList<Data> dataList = new ArrayList<>();
        CollectionReference ref = db.collection(collectionPath);
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                dataList.add(CurrentDataType.Constructor(document.getData()));
                            }
                            callback.OnCallback(dataList);
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    // "readlimit" read # docs on collection order by time
    public void readlimit(String collectionPath, int numlimit, Callback callback){
        ArrayList<Data> dataList = new ArrayList<>();
        CollectionReference ref = db.collection(collectionPath);
        ref.orderBy("time", Query.Direction.DESCENDING).limit(numlimit).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                dataList.add(CurrentDataType.Constructor(document.getData()));
                            }
                            callback.OnCallback(dataList);
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }



    public void readPagination(String collectionPath, @Nullable DocumentSnapshot latestdocument, int numlimit, @Nullable Callback2 InitialCallback, @Nullable Callback2 PaginateCallback){
        // latestitemid = contents.getcontentid
        ArrayList<Data> dataList = new ArrayList<>();
        CollectionReference ref = db.collection(collectionPath);
        if(latestdocument == null || !latestdocument.exists()){
            ref.limitToLast(numlimit).orderBy("time").
                    addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if(!value.isEmpty()){
                        for(DocumentSnapshot document : value.getDocuments()){
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            dataList.add(0,CurrentDataType.Constructor(document.getData()));
                        }
                        DocumentSnapshot finaldoc = value.getDocuments().get(0);
                        Log.d(TAG, " # " + dataList.size());
                        InitialCallback.OnCallback(dataList,finaldoc);
                    }else{
                        Log.d(TAG, "Error getting document: ", error);
                    }
                }
            });
        }
        else{
            ref.orderBy("time").limitToLast(numlimit).endBefore(latestdocument).
                    addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if(!value.isEmpty()){
                                for(DocumentSnapshot document : value.getDocuments()){
                                    Log.d(TAG, document.getId() + " => " + document.getData());
                                    dataList.add(0,CurrentDataType.Constructor(document.getData()));
                                }
                                DocumentSnapshot finaldoc = value.getDocuments().get(0);
                                PaginateCallback.OnCallback(dataList,finaldoc);
                            }else{
                                Log.d(TAG, "Error getting document: ", error);
                            }
                        }
                    });
        }
    }

    public void read(String collectionPath, String documentPath, Callback callback) { // DocumentReference - only Read one
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            Data datum = CurrentDataType.Constructor(document.getData());
                            callback.OnCallback(datum);
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    public void test(String collectionPath, Callback callback) {
        ArrayList<Data> dataList = new ArrayList<>();
        CollectionReference ref = db.collection(collectionPath);
        ref.orderBy("time", Query.Direction.DESCENDING).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                dataList.add(CurrentDataType.Constructor(document.getData()));
                            }
                            callback.OnCallback(dataList);
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    public void read(String collectionPath, String documentPath, Callback successCallback, Callback failureCallback) { // DocumentReference - only Read one
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            if (document.exists()) {
                                Data datum = CurrentDataType.Constructor(document.getData());
                                successCallback.OnCallback(datum);
                            } else {
                                failureCallback.OnCallback(null);
                            }
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    public void readIgnore(String collectionPath, String documentPath, Callback callback) { // DocumentReference - only Read one
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            try {
                                Data datum = CurrentDataType.Constructor(document.getData());
                                callback.OnCallback(datum);
                            } catch (Exception e) {
                                e.printStackTrace();
                                callback.OnCallback(null);
                            }
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }


    // write method is overloaded
    public void write(Data datum, String path, Callback callback) {
        CollectionReference ref = db.collection(path);
        ref.add(datum.DataOut())
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, task.getResult().getId() + " write Success");
                            callback.OnCallback(task.getResult().getId());
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    public void write(Data datum, String collectionPath, String documentPath, Callback callback) {
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        ref.set(datum.DataOut())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, task.getResult() + " write Success");
                            callback.OnCallback(documentPath);
                        } else {
                            Log.d(TAG, "Error getting document: ", task.getException());
                        }
                    }
                });
    }

    public void transactionUpdate(List<Object> newFieldData, String collectionPath, String documentPath, List<String> fieldPath, Callback callback1, Callback callback2) {

        Map<String, Object> temp = new HashMap<>();
        for (int i = 0; i < newFieldData.size(); i++) {
            temp.put(fieldPath.get(i), newFieldData.get(i));
        }
        DocumentReference ref = db.document(collectionPath + "/" + documentPath);
        db.runTransaction(new Transaction.Function<Object>() {
            @Override
            public Object apply(Transaction transaction) throws FirebaseFirestoreException {
                transaction.update(ref, temp);
                Log.e("TRANSATIONFAIL", transaction.toString());
                /*
                Toast.makeText(context, tempT.toString(), Toast.LENGTH_SHORT).show();
                Log.d("TRANSACTION", tempT.toString());

                 */
                return "?????????";
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

    public void readWithLocation(String path, LatLng latLng, Callback callback){

        ArrayList<Data> dataList = new ArrayList<>();

        double lat = latLng.latitude;
        double lng = latLng.longitude;

        double threshold = 0.036;

        double lower_lat = lat - threshold;
        double lower_lng = lng - threshold;
        double greater_lat = lat + threshold;
        double greater_lng = lng + threshold;

        GeoPoint lower_geoPoint = new GeoPoint(lower_lat, lower_lng);
        GeoPoint greater_geoPoint = new GeoPoint(greater_lat, greater_lng);

        CollectionReference ref = db.collection(path);
        ref.whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                dataList.add(CurrentDataType.Constructor(document.getData()));
                            }
                            callback.OnCallback(dataList);
                        }else{
                            Log.e(TAG, "Error");
                        }
                    }
                });
    }

    // No LatLng ver
    public Query CreatQuery(String collectionPath,int numlimit, @Nullable DocumentSnapshot lastdoc
        , String ContentType, Boolean isincludeexpired ){



        List<String> exclusivestate = Arrays.asList("nonAuction","ACQUIRED");

        CollectionReference ref = db.collection(collectionPath);
        if(lastdoc == null || !lastdoc.exists()){
            if(ContentType.equals("ANY")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("time").limitToLast(numlimit);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit);
                    return query;}
            }
            else if(ContentType.equals("CONTENT")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("ContentType").whereEqualTo("ContentType","Content")
                            .orderBy("time").limitToLast(numlimit);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit).whereEqualTo("ContentType","Content");
                    return query;}
            }
            else if(ContentType.equals("AUCTIONCONTENT")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                            .orderBy("time").limitToLast(numlimit);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                            .orderBy("time").limitToLast(numlimit)
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate);
                    return query;}
            }
            else{Toast.makeText(context, "Wrong ContentType inserted : " + ContentType, Toast.LENGTH_SHORT).show();
                return null;}

        }
        else{
            if(ContentType.equals("ANY")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("time").limitToLast(numlimit)
                            .endBefore(lastdoc);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit)
                            .endBefore(lastdoc);
                    return query;}
            }
            else if(ContentType.equals("CONTENT")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("time").limitToLast(numlimit).whereEqualTo("ContentType","Content")
                            .endBefore(lastdoc);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("ContentType").whereEqualTo("ContentType","Content")
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit)
                            .endBefore(lastdoc);
                    return query;}
            }
            else if(ContentType.equals("AUCTIONCONTENT")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                            .orderBy("time").limitToLast(numlimit)
                            .endBefore(lastdoc);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit)
                            .endBefore(lastdoc);
                    return query;}
            }
            else{Toast.makeText(context, "Wrong ContentType inserted : " + ContentType, Toast.LENGTH_SHORT).show();
                return null;}
        }
    }

    // Latlng ver
    public Query CreatQuery(String collectionPath,int numlimit, LatLng latLng, int rectsize, @Nullable DocumentSnapshot lastdoc
            , String ContentType, Boolean isincludeexpired ){

        List<String> exclusivestate = Arrays.asList("nonAuction","ACQUIRED");

        double threshold = 0.018 * Math.pow(2,rectsize-1);
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        double lower_lat = lat - threshold;
        double lower_lng = lng - threshold;
        double greater_lat = lat + threshold;
        double greater_lng = lng + threshold;
        GeoPoint lower_geoPoint = new GeoPoint(lower_lat, lower_lng);
        GeoPoint greater_geoPoint = new GeoPoint(greater_lat, greater_lng);



        CollectionReference ref = db.collection(collectionPath);
        if(lastdoc == null || !lastdoc.exists()){
            if(ContentType.equals("ANY")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("time").limitToLast(numlimit);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit);
                    return query;}
            }
            else if(ContentType.equals("CONTENT")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("ContentType").whereEqualTo("ContentType","Content")
                            .orderBy("time").limitToLast(numlimit);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("ContentType").whereEqualTo("ContentType","Content")
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit);
                    return query;}
            }
            else if(ContentType.equals("AUCTIONCONTENT")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                            .orderBy("time").limitToLast(numlimit);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit);
                    return query;}
            }
            else{Toast.makeText(context, "Wrong ContentType inserted : " + ContentType, Toast.LENGTH_SHORT).show();
                return null;}

        }
        else{
            if(ContentType.equals("ANY")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                    return query;}
            }
            else if(ContentType.equals("CONTENT")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("ContentType").whereEqualTo("ContentType","Content")
                            .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("ContentType").whereEqualTo("ContentType","Content")
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                    return query;}
            }
            else if(ContentType.equals("AUCTIONCONTENT")){
                if(isincludeexpired){
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                            .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                    return query;}
                else{
                    Query query = ref
                            .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                            .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                            .orderBy("auctionState").whereIn("auctionState",exclusivestate)
                            .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                    return query;}
            }
            else{Toast.makeText(context, "Wrong ContentType inserted : " + ContentType, Toast.LENGTH_SHORT).show();
                return null;}
        }
    }

    // searchText ver
    public Query CreatQuery(String collectionPath,int numlimit, LatLng latLng, int rectsize, @Nullable DocumentSnapshot lastdoc
            , String ContentType, Boolean isincludeexpired,@Nullable ArrayList<String> searchArray){

        List<String> exclusivestate = Arrays.asList("nonAuction","ACQUIRED");

        double threshold = 0.018 * Math.pow(2,rectsize-1);
        double lat = latLng.latitude;
        double lng = latLng.longitude;
        double lower_lat = lat - threshold;
        double lower_lng = lng - threshold;
        double greater_lat = lat + threshold;
        double greater_lng = lng + threshold;
        GeoPoint lower_geoPoint = new GeoPoint(lower_lat, lower_lng);
        GeoPoint greater_geoPoint = new GeoPoint(greater_lat, greater_lng);
        CollectionReference ref = db.collection(collectionPath);

        if(searchArray == null){
            if(lastdoc == null || !lastdoc.exists()){
                if(ContentType.equals("ANY")){
                    if(isincludeexpired){
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                    else{
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                }
                else if(ContentType.equals("CONTENT")){
                    if(isincludeexpired){
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","Content")
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                    else{
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","Content")
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                }
                else if(ContentType.equals("AUCTIONCONTENT")){
                    if(isincludeexpired){
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                    else{
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                }
                else{Toast.makeText(context, "Wrong ContentType inserted : " + ContentType, Toast.LENGTH_SHORT).show();
                    return null;}

            }
            else{
                if(ContentType.equals("ANY")){
                    if(isincludeexpired){
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                    else{
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                }
                else if(ContentType.equals("CONTENT")){
                    if(isincludeexpired){
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","Content")
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                    else{
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","Content")
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                }
                else if(ContentType.equals("AUCTIONCONTENT")){
                    if(isincludeexpired){
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                    else{
                        Query query = ref
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                }
                else{Toast.makeText(context, "Wrong ContentType inserted : " + ContentType, Toast.LENGTH_SHORT).show();
                    return null;}
            }
        }
        else {
            if(lastdoc == null || !lastdoc.exists()){
                if(ContentType.equals("ANY")){
                    if(isincludeexpired){
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                    else{
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                }
                else if(ContentType.equals("CONTENT")){
                    if(isincludeexpired){
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","Content")
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                    else{
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","Content")
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                }
                else if(ContentType.equals("AUCTIONCONTENT")){
                    if(isincludeexpired){
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                    else{
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit);
                        return query;}
                }
                else{Toast.makeText(context, "Wrong ContentType inserted : " + ContentType, Toast.LENGTH_SHORT).show();
                    return null;}

            }
            else{
                if(ContentType.equals("ANY")){
                    if(isincludeexpired){
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                    else{
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                }
                else if(ContentType.equals("CONTENT")){
                    if(isincludeexpired){
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","Content")
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                    else{
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","Content")
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                }
                else if(ContentType.equals("AUCTIONCONTENT")){
                    if(isincludeexpired){
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                    else{
                        Query query = ref
                                .whereArrayContainsAny("WordCase",searchArray)
                                .orderBy("geoPoint").whereGreaterThanOrEqualTo("geoPoint", lower_geoPoint).whereLessThanOrEqualTo("geoPoint", greater_geoPoint)
                                .orderBy("ContentType").whereEqualTo("ContentType","AuctionContent")
                                .orderBy("auctionState").whereEqualTo("auctionState","ACQUIRED")
                                .orderBy("time").limitToLast(numlimit).endBefore(lastdoc);
                        return query;}
                }
                else{Toast.makeText(context, "Wrong ContentType inserted : " + ContentType, Toast.LENGTH_SHORT).show();
                    return null;}
            }
        }



    }
    public void PaginationQuery(@Nullable Query query, Callback2 callback2){
        if (query != null){
            ArrayList<Data> dataList = new ArrayList<>();

            query.addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if( value !=null && !value.isEmpty() ){
                        Log.d("isEmpty", String.valueOf(value.getDocuments().size()));
                        Log.d("QuerySnapshot success", value.toString());
                        for(DocumentSnapshot document : value.getDocuments()){
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            dataList.add(0,CurrentDataType.Constructor(document.getData()));
                        }

                        Collections.sort(dataList, new Comparator<Data>() {
                            @Override
                            public int compare(Data o1, Data o2) {
                                String w1 = (String) o1.DataOut().get("time");
                                String w2 = (String) o2.DataOut().get("time");


                                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                                        .appendPattern("yyyyMMddHHmmss").appendValue(ChronoField.MILLI_OF_SECOND, 3).toFormatter();
                                LocalDateTime t1 = LocalDateTime.parse(w1,formatter);
                                LocalDateTime t2 = LocalDateTime.parse(w2,formatter);


                                if(t1.isAfter(t2)){
                                    return -1;
                                }else if (t1.isBefore(t2)){
                                    return 1;
                                }
                                return 0;
                            }
                        });

                        DocumentSnapshot finaldoc = value.getDocuments().get(0);
                        Log.d(TAG, " # " + dataList.size());
                        callback2.OnCallback(dataList,finaldoc);
                    }else{
                        Log.d(TAG, "Error getting document: ", error);
                    }
                }
            });
        }else {
            Toast.makeText(context, "Empty query inserted", Toast.LENGTH_SHORT).show();
        }

    }

}
