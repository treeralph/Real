package com.example.real;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PaddleCustomPalleteActivity extends AppCompatActivity {
    LinearLayout H1;
    LinearLayout H2;
    LinearLayout H3;
    LinearLayout H4;

    CardView ChooseImgBtn;

    CropImageView cropImageView;

    public final int REQUESTCROP = 123456;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paddle_custom_pallete);

        H1 = findViewById(R.id.PaddleCustomPaletteActivityH1LL);
        H2 = findViewById(R.id.PaddleCustomPaletteActivityH2LL);
        H3 = findViewById(R.id.PaddleCustomPaletteActivityH3LL);
        H4 = findViewById(R.id.PaddleCustomPaletteActivityH4LL);
        ChooseImgBtn = findViewById(R.id.PaddleCustomPaletteActivityChooseImgBtn);
        cropImageView = findViewById(R.id.cropImageView);

        LinearLayout[] LLL = {H1,H2,H3,H4};


        String[] colors = {"#1ABC9C", "#2ECC71", "#3498DB", "#9B59B6", "#34495E",
                "#16A085", "#27AE60", "#2980B9", "#8E44AD", "#2C3E50",
                "#F1C40F", "#E67E22", "#E74C3C", "#ECF0F1", "#95A5A6",
                "#F39C12", "#D35400", "#C0392B", "#BDC3C7", "#7F8C8D"};



        for (int i=0; i<4; i++){

            for(int j=0; j<5; j++){
                int count = i*5 + j;
                LinearLayout.LayoutParams layoutParams =
                        new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                layoutParams.weight =1;


                ImageView imageView = new ImageView(this);
                imageView.setLayoutParams(layoutParams);
                imageView.setBackgroundColor(Color.parseColor(colors[count]));
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(PaddleCustomPalleteActivity.this, colors[count], Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent();
                        intent.putExtra("COLOR",colors[count]);
                        setResult(RESULT_OK,intent);
                        finish();
                    }
                });
                LLL[i].addView(imageView);
            }
        }


        cropImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Img as Bitmap
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUESTCROP);
            }
        });
        ChooseImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bitmap cropped = cropImageView.getCroppedImage();
                Bitmap resized = cropped;
                if(getIntent().getIntExtra("REQUEST",0) == PaddleCustomActivity.REQUESTBACKGROUND){
                    resized = Bitmap.createScaledBitmap(cropped,300,540,false);
                }else if(getIntent().getIntExtra("REQUEST",0) == PaddleCustomActivity.REQUESTCENTER){
                    resized = Bitmap.createScaledBitmap(cropped,300,300,false);
                }else if(getIntent().getIntExtra("REQUEST",0) == PaddleCustomActivity.REQUESTHANDLE){
                    resized = Bitmap.createScaledBitmap(cropped,200,400,false);
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                Intent intent = new Intent();
                intent.putExtra("BITMAP",byteArray);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==RESULT_OK){
            if (requestCode == REQUESTCROP){

                Bitmap x = Bitmap.createBitmap(1,1,Bitmap.Config.ARGB_8888);
                Uri uri = data.getData();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    x = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                } catch (IOException e) {
                    e.printStackTrace();
                }

                cropImageView.setImageBitmap(x);
            }
        }
    }
}