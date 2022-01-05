package com.example.real.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.real.Callback;
import com.example.real.R;
import com.google.firebase.storage.StorageReference;

import io.grpc.Context;

public class ImgViewFromGalleryFragment extends Fragment {

    private final int USE_STORAGEREFERENCE = 0;
    private final int USE_STORAGEREFERENCE_INTENT = -1;
    private final int USE_BITMAPIMAGE = 1;

    Callback callback;
    String contentId;
    StorageReference sRef;
    Bitmap bitmap;
    int check;

    public ImgViewFromGalleryFragment(StorageReference sref) {
        sRef = sref;
        check = USE_STORAGEREFERENCE;
    }
    public ImgViewFromGalleryFragment(StorageReference sref, Callback callback) {
        sRef = sref;
        check = USE_STORAGEREFERENCE_INTENT;
        this.callback = callback;
    }

    public ImgViewFromGalleryFragment(StorageReference sref, String contentId) {
        sRef = sref;
        check = USE_STORAGEREFERENCE_INTENT;
        this.contentId = contentId;
    }
    public ImgViewFromGalleryFragment(Bitmap bitmap) {
        this.bitmap = bitmap;
        check = USE_BITMAPIMAGE;
    }

    public StorageReference getsRef() {
        return sRef;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_img_view_from_gallery, container, false);
        ImageView imageView = (ImageView)view.findViewById(R.id.imageViewFromGalleryFragment);
        if(check==USE_STORAGEREFERENCE_INTENT) {
            callback.OnCallback(imageView);
        }

        if(check == USE_STORAGEREFERENCE || check == USE_STORAGEREFERENCE_INTENT){
            Glide.with(view).load(sRef).into(imageView);
        }else{
            imageView.setImageBitmap(bitmap);
        }

        return view;
    }
}