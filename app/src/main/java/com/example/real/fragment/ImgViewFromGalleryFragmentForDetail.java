package com.example.real.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;

import com.bogdwellers.pinchtozoom.ImageMatrixTouchHandler;
import com.bogdwellers.pinchtozoom.ImageViewerCorrector;
import com.bumptech.glide.Glide;
import com.example.real.Callback;
import com.example.real.R;
import com.google.firebase.storage.StorageReference;

import pl.polidea.view.ZoomView;

public class ImgViewFromGalleryFragmentForDetail extends Fragment {

    private final int USE_STORAGEREFERENCE = 0;
    private final int USE_BITMAPIMAGE = 1;

    StorageReference sRef;
    Bitmap bitmap;
    int check;

    private ImageView imageView;

    public ImgViewFromGalleryFragmentForDetail(StorageReference sref) {
        sRef = sref;
        check = USE_STORAGEREFERENCE;
    }
    public ImgViewFromGalleryFragmentForDetail(Bitmap bitmap) {
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
        View view = inflater.inflate(R.layout.fragment_img_view_from_gallery_for_detail, container, false);

        imageView = (ImageView)view.findViewById(R.id.imageViewFromGalleryFragmentForDetail);
        ImageMatrixTouchHandler imageMatrixTouchHandler = new ImageMatrixTouchHandler(view.getContext());
        ImageViewerCorrector imageViewerCorrector = (ImageViewerCorrector) imageMatrixTouchHandler.getImageMatrixCorrector();
        imageViewerCorrector.setMaxScale(5.0f);
        imageView.setOnTouchListener(imageMatrixTouchHandler);
        //mScaleGestureDetector = new ScaleGestureDetector(view.getContext(), new ScaleListener());

        if(check == USE_STORAGEREFERENCE){
            Glide.with(view).load(sRef).into(imageView);
        }else{
            imageView.setImageBitmap(bitmap);
        }

        return view;
    }
}