package com.example.real;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class PopUpActivity extends Activity {

    RelativeLayout relativeLayout;
    LinearLayout modifyBtn;
    LinearLayout deleteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_up);

        relativeLayout = findViewById(R.id.popUpActivityRelativeLayout);
        modifyBtn = findViewById(R.id.popUpActivityModifyButton);
        deleteBtn = findViewById(R.id.popUpActivityDeleteButton);

        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }
}