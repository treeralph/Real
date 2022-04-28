package com.example.real.tool;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.real.Callback;
import com.example.real.R;
import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.fragment.MapFragment;

public class LocationActivity extends AppCompatActivity {

    private String TAG = "LocationActivity";

    ViewPager viewPager;
    ViewPagerAdapter adapter;

    MapFragment mapFragment;
    String location;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        viewPager = findViewById(R.id.locationActivityViewPager);
        adapter = new ViewPagerAdapter(getSupportFragmentManager());

        mapFragment = new MapFragment(this, new Callback() {
            @Override
            public void OnCallback(Object object) {

            }
        }, new Callback() {
            @Override
            public void OnCallback(Object object) {
                View view = (View) object;
                CardView cardView = view.findViewById(R.id.mapFragmentCheckButton);
                cardView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        location = mapFragment.getFocusAddress();

                        Intent intent = new Intent();
                        intent.putExtra("Location", location);
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                });

            }
        });

        adapter.addItem(mapFragment);
        viewPager.setAdapter(adapter);
    }
}