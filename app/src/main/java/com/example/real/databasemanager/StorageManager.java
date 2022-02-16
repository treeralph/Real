package com.example.real.databasemanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.telecom.Call;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.real.Callback;
import com.example.real.tool.ImageSizeTool;
import com.example.real.tool.NumberingMachine;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

public class StorageManager {

    FirebaseStorage storage;
    StorageReference ref;
    private final String TAG = "FIRESTOREMANAGER";
    private Context Context;
    private String DataType;
    private String Uid;


    // Operator choose ItemSize
    long ITEMSIZE;
    final long ONE_MEGABYTE = 1024*1024;
    final long QUARTER_MEGABYTE = 512*512;
    final long PROFILESIZE = 64*64;

    public StorageManager(Context context, String dataType, String uid){

        storage = FirebaseStorage.getInstance();
        ref = storage.getReference();
        Context = context;
        DataType = dataType;
        Uid = uid;
    }

    public void downloadV2(String path, String subPath, Callback callback){

        StorageReference cRef = ref.child(path + "/" + subPath);

    }

    public void downloadImg2View(String path, String subPath, ImageView imageView, Callback callback){
        StorageReference cRef = ref.child(path + "/" + subPath);
        Glide.with(Context).load(cRef).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(imageView);
        callback.OnCallback(1);
    }

    public void downloadImg2View(String path, ImageView imageView, Callback callback){
        StorageReference cRef = ref.child(path);
        Glide.with(Context).load(cRef).into(imageView);
        callback.OnCallback(1);
    }


    public void download(String path, String subPath, Callback callback){

        //StorageReference ref = storage.getReference();
        StorageReference cRef = ref.child(path + "/" + subPath);

        cRef.getBytes(ONE_MEGABYTE*10).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(TAG,  "Storage download Success");
                Bitmap tempBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                callback.OnCallback(tempBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error downloading Storage data: " + e.getMessage());
            }
        });
    }

    public void download(String path, String subPath, int sampleSize, Callback callback){

        //StorageReference ref = storage.getReference();
        StorageReference cRef = ref.child(path + "/" + subPath);

        cRef.getBytes(ONE_MEGABYTE*50).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                Log.d(TAG,  "Storage download Success");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = sampleSize;

                Bitmap tempBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);
                callback.OnCallback(tempBitmap);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error downloading Storage data: " + e.getMessage());
            }
        });
    }

    public void download(String path, Callback callback){

        ArrayList<Bitmap> BitmapList = new ArrayList<>();

        //StorageReference ref = storage.getReference();
        StorageReference cRef = ref.child(path);

        // Raise asynchronous issue in duplicated server access
        cRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        Log.d(TAG,  "Storage read Success");
                        for(StorageReference item : listResult.getItems()){
                            item.getBytes(ONE_MEGABYTE)
                                    .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                                        @Override
                                        public void onSuccess(byte[] bytes) {
                                            Log.d(TAG,  "Storage download Success");
                                            Bitmap tempBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                                            BitmapList.add(tempBitmap);
                                            callback.OnCallback(BitmapList);
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "Error downloading Storage data: " + e.getMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Error getting Storage data: " + e.getMessage());
                    }
                });
    }

    public void upload(String path, Bitmap image, Callback callback){

        //StorageReference ref = storage.getReference();
        StorageReference mRef = ref.child(path);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error getting Storage data: " + e.getMessage());
                callback.OnCallback("1");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG,  "Storage write Success");
                callback.OnCallback("0");
            }
        });
    }

    public void upload(String path, Bitmap image, int threshold, Callback callback){

        ImageSizeTool imageSizeTool = new ImageSizeTool(threshold);
        int compressRatio = imageSizeTool.getRatio(image);

        int compressQuality = (int)(100/compressRatio);
        if(compressQuality < 1){ compressQuality = 1; } else{ }

        //StorageReference ref = storage.getReference();
        StorageReference mRef = ref.child(path);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, (int)(100/compressRatio), baos);

        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Error getting Storage data: " + e.getMessage());
                callback.OnCallback("1");
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.d(TAG,  "Storage write Success");
                callback.OnCallback("0");
            }
        });
    }

    public void delete(String path, String subPath, Callback callback){

        StorageReference aRef = ref.child("/" + path + "/" + subPath);
        aRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        NumberingMachine nm = new NumberingMachine();
                        for(StorageReference sRef : listResult.getPrefixes()){
                            sRef.listAll()
                                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                                        @Override
                                        public void onSuccess(ListResult listResult_1) {
                                            for(StorageReference tRef : listResult_1.getItems()){
                                                tRef.delete()
                                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                            @Override
                                                            public void onSuccess(Void unused) {
                                                                nm.add();
                                                                if(listResult.getPrefixes().size() * listResult_1.getItems().size() == nm.getNumber()){
                                                                    Log.d(TAG + "/delete", "Content is deleted");
                                                                    callback.OnCallback(null);
                                                                }

                                                            }
                                                        })
                                                        .addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                Log.e(TAG + "/delete_2", e.getMessage());
                                                            }
                                                        });
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e(TAG + "/delete_1", e.getMessage());
                                        }
                                    });
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG + "/delete_0", e.getMessage());
                    }
                });
    }


}
