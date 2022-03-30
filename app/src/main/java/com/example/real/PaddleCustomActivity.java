package com.example.real;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;

import com.example.real.tool.CreatePaddle;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class PaddleCustomActivity extends AppCompatActivity {
    ImageView paddleimageview;
    CardView applybtn;
    CreatePaddle createPaddle;

    Bitmap UserBackground;
    Bitmap UserCenter;
    Bitmap UserHandle;
    int Paddle_Size_x;

    ImageView PreviewBackground;
    ImageView PreviewCenter;
    ImageView PreviewHandle;

    static final int REQUESTBACKGROUND = 0325;
    static final int REQUESTCENTER = 0326;
    static final int REQUESTHANDLE = 0327;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paddle_custom);

        paddleimageview = findViewById(R.id.PaddleCustomActivitypaddleIV);
        applybtn = findViewById(R.id.PaddleCustomActivityApplyButton);
        PreviewBackground = findViewById(R.id.PaddleCustomActivityPreviewBackground);
        PreviewCenter = findViewById(R.id.PaddleCustomActivityPreviewCenter);
        PreviewHandle = findViewById(R.id.PaddleCustomActivityPreviewHandle);

        // Initial Setting
        Bitmap Test = Bitmap.createBitmap(100,100,Bitmap.Config.ARGB_8888);
        Test.eraseColor(0xfffaeb87);
        Bitmap InitialBG = Test;
        //Bitmap InitialBG = BitmapFactory.decodeResource(this.getResources(), R.drawable.platinum);
        Bitmap InitialCenter = BitmapFactory.decodeResource(this.getResources(), R.drawable.mango_flaticon_1032525);
        Bitmap InitialHandle = BitmapFactory.decodeResource(this.getResources(), R.drawable.aucto1);

        UserBackground = InitialBG;
        UserCenter = InitialCenter;
        UserHandle = InitialHandle;

        PreviewBackground.setImageBitmap(Bitmap.createScaledBitmap(UserBackground,50,50,true));
        PreviewCenter.setImageBitmap(Bitmap.createScaledBitmap(UserCenter,50,50,true));
        PreviewHandle.setImageBitmap(Bitmap.createScaledBitmap(UserHandle,50,50,true));

        paddleimageview.post(new Runnable() {
            @Override
            public void run() {
                Paddle_Size_x = paddleimageview.getWidth();
                createPaddle = new CreatePaddle();
                Bitmap InitialPaddle = createPaddle.createPaddle(UserBackground,UserCenter,UserHandle,Paddle_Size_x);

                paddleimageview.setScaleType(ImageView.ScaleType.FIT_XY);
                paddleimageview.setAdjustViewBounds(true);
                paddleimageview.setImageBitmap(InitialPaddle);
            }
        });






        // Click listeners
        PreviewBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Img as Bitmap
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUESTBACKGROUND);

            }
        });
        PreviewCenter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Img as Bitmap
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUESTCENTER);

            }
        });
        PreviewHandle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Img as Bitmap
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUESTHANDLE);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if (requestCode == REQUESTBACKGROUND){

                // Set UserBackground = Bitmap;

                Uri uri = data.getData();
                try {
                    UserBackground = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /*
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    UserBackground = BitmapFactory.decodeStream(in);
                    in.close();
                }catch(Exception e){ }

                 */
                // Make Paddle & Set on View
                Bitmap NewPaddle = createPaddle.createPaddle(UserBackground,UserCenter,UserHandle,Paddle_Size_x);
                paddleimageview.setImageBitmap(NewPaddle);
                // Set on Preview
                PreviewBackground.setImageBitmap(Bitmap.createScaledBitmap(UserBackground,50,50,true));
            }


            else if(requestCode == REQUESTCENTER){

                // Set UserCenter = Bitmap;
                Uri uri = data.getData();
                try {
                    UserCenter = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                /*
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    UserCenter = BitmapFactory.decodeStream(in);
                    in.close();
                }catch(Exception e){ }

                 */
                // Make Paddle & Set on View
                Bitmap NewPaddle = createPaddle.createPaddle(UserBackground,UserCenter,UserHandle,Paddle_Size_x);
                paddleimageview.setImageBitmap(NewPaddle);
                // Set on Preview
                PreviewCenter.setImageBitmap(Bitmap.createScaledBitmap(UserCenter,50,50,true));
            }


            else if(requestCode == REQUESTHANDLE){

                // Set UserHandle = Bitmap;
                try{
                    InputStream in = getContentResolver().openInputStream(data.getData());
                    UserHandle = BitmapFactory.decodeStream(in);
                    in.close();
                }catch(Exception e){ }
                // Make Paddle & Set on View
                Bitmap NewPaddle = createPaddle.createPaddle(UserBackground,UserCenter,UserHandle,Paddle_Size_x);
                paddleimageview.setImageBitmap(NewPaddle);
                // Set on Preview
                PreviewHandle.setImageBitmap(Bitmap.createScaledBitmap(UserHandle,50,50,true));
            }
        }

    }
}