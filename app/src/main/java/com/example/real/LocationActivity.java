package com.example.real;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.real.Callback;
import com.example.real.R;
import com.example.real.adapter.ViewPagerAdapter;
import com.example.real.fragment.MapFragment;
import com.naver.maps.geometry.LatLng;

public class LocationActivity extends AppCompatActivity {

    private String TAG = "LocationActivity";

    RelativeLayout relativeLayout;
    ViewPager viewPager;
    ViewPagerAdapter adapter;

    MapFragment mapFragment;
    String location;
    String adm_cd;
    LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        relativeLayout = findViewById(R.id.locationActivityRelativeLayout);
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
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
                        adm_cd = mapFragment.getFocusAdmCd();
                        latLng = mapFragment.getFocusLatLng();

                        String stringLatLng = String.valueOf(latLng.latitude) + "," + String.valueOf(latLng.longitude);
                        Intent intent = new Intent();
                        intent.putExtra("Location", location);
                        intent.putExtra("Adm_cd", adm_cd);
                        intent.putExtra("LatLng", stringLatLng);

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