package com.example.real;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.api.Distribution;

public class PopUpActivityForContents extends Activity {

    RelativeLayout relativeLayout;
    LinearLayout makeContentBtn;
    LinearLayout makeAuctionBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up_for_contents);

        relativeLayout = findViewById(R.id.popUpActivityForContentsRelativeLayout);
        makeContentBtn = findViewById(R.id.popUpActivityForContentsMakeContentButton);
        makeAuctionBtn = findViewById(R.id.popUpActivityForContentsMakeAuctionContentButton);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        makeContentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PopUpActivityForContents.this, MakeContentActivity.class);
                startActivity(intent);
                finish();
            }
        });

        makeAuctionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PopUpActivityForContents.this, MakeAuctionContentActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}